package com.imcode.spring;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.accept.AbstractMappingContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

/**
 * A ContentNegotiationStrategy that uses the path extension of the URL to
 * determine what media types are requested. The path extension is first looked
 * up in the map of media types provided to the constructor. If that fails, the
 * Java Activation framework is used as a fallback mechanism.
 *
 * <p>
 * The presence of the Java Activation framework is detected and enabled
 * automatically but the {@link #setUseJaf(boolean)} property may be used to
 * override that setting.
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 */
public class PathExtensionContentNegotiationStrategy extends AbstractMappingContentNegotiationStrategy {

    private static final boolean JAF_PRESENT = ClassUtils.isPresent("javax.activation.FileTypeMap",
            PathExtensionContentNegotiationStrategy.class.getClassLoader());

    private static final Log logger = LogFactory.getLog(PathExtensionContentNegotiationStrategy.class);

    private static final UrlPathHelper urlPathHelper = new UrlPathHelper();

    static {
        urlPathHelper.setUrlDecode(false);
    }

    private boolean useJaf = JAF_PRESENT;


    /**
     * Create an instance with the given extension-to-MediaType lookup.
     * @throws IllegalArgumentException if a media type string cannot be parsed
     */
    public PathExtensionContentNegotiationStrategy(Map<String, MediaType> mediaTypes) {
        super(mediaTypes);
    }

    /**
     * Create an instance without any mappings to start with. Mappings may be added
     * later on if any extensions are resolved through the Java Activation framework.
     */
    public PathExtensionContentNegotiationStrategy() {
        super(null);
    }

    /**
     * Indicate whether to use the Java Activation Framework to map from file extensions to media types.
     * <p>Default is {@code true}, i.e. the Java Activation Framework is used (if available).
     */
    public void setUseJaf(boolean useJaf) {
        this.useJaf = useJaf;
    }

    @Override
    protected String getMediaTypeKey(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        if (servletRequest == null) {
            logger.warn("An HttpServletRequest is required to determine the media type key");
            return null;
        }
        String path = urlPathHelper.getLookupPathForRequest(servletRequest);
        List<String> fileTypes = getAllFileExtensions();
        for (String fileType : fileTypes) {
            String formatVariable = "/" + fileType + "/";
            if (path.indexOf(formatVariable) != -1) {
                return fileType;
            }
        }

        return null;

//        String filename = WebUtils.extractFullFilenameFromUrlPath(path);
//        String extension = StringUtils.getFilenameExtension(filename);
//        return (StringUtils.hasText(extension)) ? extension.toLowerCase(Locale.ENGLISH) : null;
    }

    @Override
    protected void handleMatch(String extension, MediaType mediaType) {
    }

    @Override
    protected MediaType handleNoMatch(NativeWebRequest webRequest, String extension) {
        if (this.useJaf) {
            MediaType jafMediaType = JafMediaTypeFactory.getMediaType("file." + extension);
            if (jafMediaType != null && !MediaType.APPLICATION_OCTET_STREAM.equals(jafMediaType)) {
                return jafMediaType;
            }
        }
        return null;
    }


    /**
     * Inner class to avoid hard-coded dependency on JAF.
     */
    private static class JafMediaTypeFactory {

        private static final FileTypeMap fileTypeMap;

        static {
            fileTypeMap = initFileTypeMap();
        }

        /**
         * Find extended mime.types from the spring-context-support module.
         */
        private static FileTypeMap initFileTypeMap() {
            Resource resource = new ClassPathResource("org/springframework/mail/javamail/mime.types");
            if (resource.exists()) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Loading Java Activation Framework FileTypeMap from " + resource);
                }
                InputStream inputStream = null;
                try {
                    inputStream = resource.getInputStream();
                    return new MimetypesFileTypeMap(inputStream);
                }
                catch (IOException ex) {
                    // ignore
                }
                finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        }
                        catch (IOException ex) {
                            // ignore
                        }
                    }
                }
            }
            if (logger.isTraceEnabled()) {
                logger.trace("Loading default Java Activation Framework FileTypeMap");
            }
            return FileTypeMap.getDefaultFileTypeMap();
        }

        public static MediaType getMediaType(String filename) {
            String mediaType = fileTypeMap.getContentType(filename);
            return (StringUtils.hasText(mediaType) ? MediaType.parseMediaType(mediaType) : null);
        }
    }

}