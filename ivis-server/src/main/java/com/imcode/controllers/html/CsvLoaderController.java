package com.imcode.controllers.html;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imcode.controllers.html.form.upload.FileOption;
import com.imcode.controllers.html.form.upload.FileUploadForm;
import com.imcode.controllers.html.form.upload.FileUploadOptionsForm;
import com.imcode.controllers.html.form.upload.loaders.EntityLoader;
import com.imcode.controllers.html.form.upload.loaders.LoaderService;
import com.imcode.entities.*;
//import com.imcode.misc.MutablePropertyFilter;
import com.imcode.entities.interfaces.JpaPersonalizedEntity;
import com.imcode.misc.UploadFileManager;
import com.imcode.services.GenericService;
import com.imcode.services.GuardianService;
import com.imcode.services.PersonService;
import com.imcode.services.csv.CsvFieldSetMapper;
import com.imcode.services.csv.GuardianFieldSetMapper;
import com.sun.net.httpserver.HttpPrincipal;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by vitaly on 01.12.15.
 */
@Controller
@RequestMapping("/csv")
public class CsvLoaderController {
    private static final String UPLOAD_FILE_MANAGER = "uploadFileManager";
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private LoaderService loaderService;

    private Map<Class<?>, Supplier<FieldSetMapper>> fieldSetMappers;


    @RequestMapping(method = RequestMethod.GET)
    public String step1() {
        return "csv/file_upload_step1";
    }

    @RequestMapping(value = "/step2", method = RequestMethod.POST)
    public String step2(
            @ModelAttribute("uploadForm") FileUploadForm uploadForm,
            Model model,
            HttpServletRequest request) {

        List<MultipartFile> files = uploadForm.getFiles();
        Map<Class<?>, Set<String>> typeMap = loaderService.stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getAllowedFieldSet()));
        Set<String> mockEntityProperties = typeMap.get(Person.class);
        List<FileOption> fileOptionList = new ArrayList<>();
        FileUploadOptionsForm fileUploadOptionsForm = new FileUploadOptionsForm();
        fileUploadOptionsForm.setFileOptionList(fileOptionList);
        //todo chabge on real user
        Principal principal = new HttpPrincipal("Admin", "pass");

        if (null != files && files.size() > 0) {
            UploadFileManager fileManager = getUploadFileManeger(request);
            HttpSession session = request.getSession(true);
            session.setAttribute(UPLOAD_FILE_MANAGER, fileManager);

            for (MultipartFile multipartFile : files) {
                String fileId = fileManager.put(multipartFile);
                fileOptionList.add(FileOption.of(fileId, multipartFile, 10));
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String typeMapJson = null;
        try {
            typeMapJson = objectMapper.writeValueAsString(typeMap);
        } catch (JsonProcessingException e) {
            //todo log this thing
            e.printStackTrace();
        }

        model.addAttribute("fileUploadOptionsForm", fileUploadOptionsForm);
        model.addAttribute("typeMap", typeMap);
        model.addAttribute("typeMapJson", typeMapJson);
        model.addAttribute("mockEntityProperties", mockEntityProperties);

        return "csv/file_upload_step2";
    }
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/step3", method = RequestMethod.POST)
    public String step3(
            @ModelAttribute("fileUploadOptionsForm") FileUploadOptionsForm optionsForm,
            Model model,
            HttpServletRequest request,
            Principal principal) {

//        System.out.println("hello");
        UploadFileManager uploadFileManager = getUploadFileManeger(request);
        List<List> resultList = new ArrayList<>();

        for (FileOption fileOption : optionsForm.getFileOptionList()) {
            List<Object> result = new ArrayList<>();
            resultList.add(result);
            Path file = uploadFileManager.getFile(fileOption.getFileId());

            if (file == null || Files.notExists(file)) {
                throw new RuntimeException("file not found");
            }

            EntityLoader<?> entityLoader = loaderService.getLoader(fileOption.getType());

            if (entityLoader == null) {
                throw new RuntimeException("loader not found");
            }

            FileSystemResource resource = new FileSystemResource(file.toFile());
            FlatFileItemReader<?> itemReader = new FlatFileItemReader<>();
            itemReader.setResource(resource);

            DefaultLineMapper lineMapper = new DefaultLineMapper<>();
            final DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
            tokenizer.setNames(fileOption.getColumnNameList().toArray(new String[fileOption.getColumnNameList().size()]));
            lineMapper.setLineTokenizer(tokenizer);
            FieldSetMapper fieldMapper = entityLoader.getFieldSetMapper(fileOption);
            lineMapper.setFieldSetMapper(fieldMapper);

            itemReader.setLineMapper(lineMapper);
            itemReader.setLinesToSkip(fileOption.getSkipRows());
            itemReader.open(new ExecutionContext());

            Object g = null;
            try {
                while ((g = itemReader.read()) != null) {
                    result.add(g);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ItemWriter itemWriter = entityLoader.getItemWriter();

            try {
                itemWriter.write(result);
            } catch (Exception e) {
                e.printStackTrace();
            }

            fileOption.setResult(result);
        }
        model.addAttribute("fileUploadOptionsForm", optionsForm);
        return "csv/file_upload_step3";
    }

    private UploadFileManager getUploadFileManeger(HttpServletRequest request) {
        UploadFileManager uploadFileManager = (UploadFileManager) request.getSession(true).getAttribute(UPLOAD_FILE_MANAGER);

        if (uploadFileManager == null) {
            uploadFileManager = new UploadFileManager(request.getUserPrincipal());
        }

        return uploadFileManager;
    }

    public static Set<String> getBeanFields(Class<?> clazz) {
        Set<String> result = new TreeSet<>();

        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            Set<String> fieldSet = Arrays.stream(fields).map(Field::getName).collect(Collectors.toSet());
            result.addAll(fieldSet);
            clazz = clazz.getSuperclass();
        }

        return result;
    }

    public static Set<String> getBeanFieldsRecursively(Class<?> clazz, String prefix) {
        Set<String> result = new TreeSet<>();
        prefix = prefix.length() == 0 ? prefix : prefix + ".";

        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                String fieldName = prefix + field.getName();
                Class<?> fieldType = field.getType();

                result.add(fieldName);
                if (!ClassUtils.isPrimitiveOrWrapper(fieldType))
                    result.addAll(getBeanFieldsRecursively(fieldType, fieldName));
            }

            clazz = clazz.getSuperclass();
        }

        return result;
    }

    public static Set<String> getBeanFieldsRecursively(Class<?> clazz) {
        return getBeanFieldsRecursively(clazz, "");
    }

//    public static List<String> getMutablePropertyNames(Class<?> clazz) {
//        List<Method> getters = getMutableProperties(clazz);
//        List<String> propertyNames = getters.stream().map(method -> method.getName().substring(2)).collect(Collectors.toList());
//
//        return propertyNames;
//    }

//    public static List<Method> getMutableProperties(Class<?> bean) {
//        Method[] allMethods = bean.getClass().getMethods();
//        Predicate<Method> mutablePropertyFilter = new MutablePropertyFilter(bean);
//
//        List<Method> methodList = Arrays.stream(allMethods).filter(mutablePropertyFilter).collect(Collectors.toList());
//
//        return methodList;
//    }


    @SuppressWarnings("unchecked")
    public <T> FieldSetMapper<T> getMapper(Class<T> clazz, FileOption fileOption) {
        CsvFieldSetMapper<T> fieldSetMapper = (CsvFieldSetMapper<T>) new GuardianFieldSetMapper();
        fieldSetMapper.setFileOption(fileOption);
        GuardianService guardianService = applicationContext.getBean(GuardianService.class);
        fieldSetMapper.setService((GenericService<T, ?>) guardianService);
        return fieldSetMapper;
    }

    @PostConstruct
    public void init() {
//        fieldSetMappers.put(Guardian.class, () -> {applicationContext.getBean(GuardianFieldSetMapper.class)});
    }

    public static void main(String[] args) {
//        Set<String> properties = getBeanFieldsRecursively(Pupil.class);
//        System.out.println(properties);
    }

}

class PersonalizedItemWriter<T extends JpaPersonalizedEntity> implements ItemWriter<T> {
    private final GenericService<T, ?> entityService;
    private final PersonService personService;

    PersonalizedItemWriter(GenericService<T, ?> entityService, PersonService personService) {
        this.entityService = entityService;
        this.personService = personService;
    }

    @Override
    public void write(List<? extends T> items) throws Exception {
        for (T item : items) {
            Person person = item.getPerson();
//            if (person.getId() == null) {
                person = personService.save(person);
                item.setPerson(person);
//            }
            entityService.save(item);
        }
    }
}