<%@ page import="com.imcode.entities.Statement,
                 com.imcode.services.StatementService" pageEncoding="UTF-8" %>
<%@ page import="imcode.server.Imcms" %>
<%@ page import="imcode.services.IvisServiceFactory" %>
<%@ page import="imcode.services.utils.IvisOAuth2Utils" %>
<%@ page import="org.springframework.security.oauth2.client.resource.UserRedirectRequiredException" %>

<%@taglib prefix="imcms" uri="imcms" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<imcms:variables/>
<jsp:include page="ivis_header.jsp"/>
<%
    if (IvisOAuth2Utils.isTokenGood(request)) {
        IvisServiceFactory factory = IvisOAuth2Utils.getServiceFactory(request);
        StatementService service = factory.getService(StatementService.class);
        Statement statement = null;
        try {
            statement = service.find(Long.valueOf(request.getParameter("id")));
        } catch (UserRedirectRequiredException e) {
            IvisOAuth2Utils.setAccessToken(session, null);
            response.sendRedirect(Imcms.getServerProperties().getProperty("ClientAddress") + "/servlet/StartDoc?meta_id=" + viewing.getTextDocument().getId());
            return;
        }
        request.setAttribute("statement", statement);
    }
%>
<h1>Application</h1>
<c:if test="${not empty pupil}">
    <h1>Pupil - ${pupil.person.firstName} ${pupil.person.lastName}</h1>

    <div class="tabs">
        <div class="tab" data-tab-page-id="basicDataTabPage">
            Basic data
        </div>
        <div class="tab" data-tab-page-id="guardiansTabPage">
            Guardians
        </div>
        <div class="tab" data-tab-page-id="schoolAndClassTabPage">
            School & class
        </div>
        <div class="tab" data-tab-page-id="schoolTransportTabPage">
            School transport
        </div>
        <div class="tab" data-tab-page-id="loggTabPage">
            Logg
        </div>
    </div>
    <%--<form:form modelAttribute="pupil" action="<%=Imcms.getServerProperties().getProperty("ClientAddress")%>/api/content/ivis/pupils" method="post">--%>
    <form:form modelAttribute="pupil" action="${clientAddress}/api/content/ivis/pupils" method="post" id="pupil-form">
        <div id="basicDataTabPage" class="tab-page">
            <form:hidden path="id" cssErrorClass="error"/>
                <%--<div class="field">--%>
                <%--<form:label path=""></form:label>--%>
                <%--<form:input path="" cssErrorClass="error"/>--%>
                <%--<form:errors path="" cssClass="error-description"/>--%>
                <%--</div>--%>
            <div id="pupil.personField">
                <c:set var="personPath" value="person"/>
                <c:set var="person" value="${pupil.person}"/>
                <input id="person" type="hidden" name="${personPath}.id" value="${person.id}">

                <div class="field">
                    <form:label path="${personPath}.personalId">Personal ID</form:label>
                    <form:input data-rule-maxlength="255" path="${personPath}.personalId" cssErrorClass="error"/>
                    <form:errors path="${personPath}.personalId" cssClass="error-description"/>
                </div>
                <div class="field">
                    <form:label path="${personPath}.lastName">Last name</form:label>
                    <form:input data-rule-maxlength="255" path="${personPath}.lastName" cssErrorClass="error"/>
                    <form:errors path="${personPath}.lastName" cssClass="error-description"/>
                </div>
                <div class="field">
                    <form:label path="${personPath}.firstName">
                        First name
                    </form:label>
                    <form:input data-rule-maxlength="255" path="${personPath}.firstName" cssErrorClass="error"/>
                    <form:errors path="${personPath}.firstName" cssClass="error-description"/>
                </div>

                <h2>Addresses:</h2>
                <c:set var="containerId" value="${personPath}.addresses"/>
                <div id="${containerId}">
                    <c:forEach var="address" items="${person.addresses}" varStatus="status">
                        <c:set var="subContainerId" value="${containerId}${status.index}Field"/>
                        <div class="field" id="${subContainerId}" data-index="${status.index}">
                                <%--<form:hidden path="${personPath}.addresses[${status.index}].id"/>--%>

                            <form:select path="${containerId}[${status.index}].addressType"
                                         items="${addressTypeEnum}"/>
                            <button class="negative" type="button"
                                    onclick="ivis.ui.removeContainer('${subContainerId}');">Remove
                            </button>
                                <%--<input type="hidden" name="${containerId}[${status.index}].addressType" value="${address.addressType}"/>--%>

                            <form:label path="${containerId}[${status.index}].careOf">c/o</form:label>
                            <form:input data-rule-maxlength="255" path="${containerId}[${status.index}].careOf"
                                        cssErrorClass="error"/>
                            <form:errors path="${containerId}[${status.index}].careOf" cssClass="error-description"/>

                            <form:label path="${containerId}[${status.index}].street">Street</form:label>
                            <form:input data-rule-maxlength="255" path="${containerId}[${status.index}].street"
                                        cssErrorClass="error"/>
                            <form:errors path="${containerId}[${status.index}].street" cssClass="error-description"/>

                            <form:label path="${containerId}[${status.index}].postalCode">Postal code</form:label>
                            <form:input data-rule-maxlength="255" data-rule-digits="true"
                                        path="${containerId}[${status.index}].postalCode" cssErrorClass="error"
                                        data-rule-postalcodeIT="true"/>
                            <form:errors path="${containerId}[${status.index}].postalCode"
                                         cssClass="error-description"/>

                            <form:label path="${containerId}[${status.index}].city">City</form:label>
                            <form:input data-rule-maxlength="255" path="${containerId}[${status.index}].city"
                                        cssErrorClass="error"/>
                            <form:errors path="${containerId}[${status.index}].city" cssClass="error-description"/>

                            <form:label
                                    path="${containerId}[${status.index}].municipalityCode">Municipality code</form:label>
                            <form:input data-rule-maxlength="255"
                                        path="${containerId}[${status.index}].municipalityCode" cssErrorClass="error"/>
                            <form:errors path="${containerId}[${status.index}].municipalityCode"
                                         cssClass="error-description"/>
                        </div>
                    </c:forEach>
                    <button class="positive" type="button" onclick="ivis.ui.addAddress('${containerId}');">Add</button>
                </div>

                <h2>Phones:</h2>
                <c:set var="containerId" value="${personPath}.phones"/>
                <div id="${containerId}">
                    <c:forEach var="phone" items="${person.phones}" varStatus="status">
                        <c:set var="subContainerId" value="${containerId}${status.index}Field"/>
                        <div class="field" id="${subContainerId}" data-index="${status.index}">
                                <%--<form:hidden path="${personPath}.phones[${status.index}].id"/>--%>

                                <%--<input type="hidden" name="${personPath}.phones[${status.index}].communicationType" value="${phone.communicationType}"/>--%>
                            <form:select path="${containerId}[${status.index}].communicationType"
                                         items="${communicationTypeEnum}"/>
                            <button class="negative" type="button"
                                    onclick="ivis.ui.removeContainer('${subContainerId}');">Remove
                            </button>
                            <form:label path="${containerId}[${status.index}].number">Phone</form:label>
                            <form:input path="${containerId}[${status.index}].number" cssErrorClass="error"
                                        data-rule-pattern="\s*((\+([\s-]*\d[\s-]*){2}|([\s-]*\d[\s-]*)?)?((\(([\s-]*\d[\s-]*){3}\))|([\s-]*\d[\s-]*){3}))?([\s-]*\d[\s-]*){7}"/>
                            <form:errors path="${containerId}[${status.index}].number" cssClass="error-description"/>
                        </div>
                    </c:forEach>
                    <button class="positive" type="button" onclick="ivis.ui.addPhone('${containerId}');">Add</button>
                </div>

                <h2>Emails:</h2>
                <c:set var="containerId" value="${personPath}.emails"/>
                <div id="${containerId}">
                    <c:forEach var="email" items="${person.emails}" varStatus="status">
                        <c:set var="subContainerId" value="${containerId}${status.index}Field"/>
                        <div class="field" id="${subContainerId}" data-index="${status.index}">
                                <%--<form:hidden path="person.${containerId}[${status.index}].id"/>--%>

                            <form:select path="${containerId}[${status.index}].communicationType"
                                         items="${communicationTypeEnum}"/>
                            <button class="negative" type="button"
                                    onclick="ivis.ui.removeContainer('${subContainerId}');">Remove
                            </button>
                            <form:label
                                    path="${containerId}[${status.index}].address">Email</form:label>
                            <form:input data-rule-email="true" path="${containerId}[${status.index}].address"
                                        cssErrorClass="error"/>
                            <form:errors path="${containerId}[${status.index}].address" cssClass="error-description"/>
                        </div>
                    </c:forEach>
                    <button class="positive" type="button" onclick="ivis.ui.addEmail('${containerId}');">Add</button>
                </div>
            </div>
            <div class="buttons">
                <button class="positive" type="submit">Save</button>
                <a class="button neutral" type="/pupils">Cancel</a>
            </div>
        </div>
        <div id="guardiansTabPage" class="tab-page">
            <div id="guardians">
                <c:forEach var="guardian" items="${pupil.guardians}" varStatus="status">
                    <c:set var="guardianItemId" value="guardians${status.index}"/>
                    <c:set var="guardianItemName" value="guardians[${status.index}]"/>
                    <h2 onclick="ivis.ui.toggleDiv('${guardianItemId}')">Guardian ${status.index + 1}</h2>

                    <div id="${guardianItemId}" data-index="${status.index}">
                        <div class="checkbox">
                            <input id="${guardianItemId}.solo"
                                   type="checkbox" ${pupil.guardians.size() == 1 ? 'checked': ''}
                                   onchange="ivis.ui.disableSoloGuardian('${guardianItemId}.solo', '${guardianItemId}', 'guardians');">
                            <label for="${guardianItemId}.solo">Solo guardian</label>
                        </div>
                            <%--<input name="guardianList[${status.index}]" value="${guardian.id}">--%>
                            <%--================================================================================--%>
                            <%--<input type="hidden" name="${guardianItemName}" value="${guardian.id}">--%>
                        <input type="hidden" name="guardianList[${status.index}].id" value="${guardian.id}">

                        <div id="${guardianItemId}.personField">
                            <c:set var="personPath" value="guardians[${status.index}].person"/>
                            <c:set var="person" value="${guardian.person}"/>
                            <input id="person.id" type="hidden" name="${personPath}.id" value="${person.id}">

                            <div class="field">
                                <form:label path="${personPath}.personalId">Personal ID</form:label>
                                <form:input data-rule-maxlength="255" path="${personPath}.personalId"
                                            cssErrorClass="error"/>
                                <form:errors path="${personPath}.personalId" cssClass="error-description"/>
                            </div>
                            <div class="field">
                                <form:label path="${personPath}.lastName">Last name</form:label>
                                <form:input data-rule-maxlength="255" path="${personPath}.lastName"
                                            cssErrorClass="error"/>
                                <form:errors path="${personPath}.lastName" cssClass="error-description"/>
                            </div>
                            <div class="field">
                                <form:label path="${personPath}.firstName">
                                    First name
                                </form:label>
                                <form:input data-rule-maxlength="255" path="${personPath}.firstName"
                                            cssErrorClass="error"/>
                                <form:errors path="${personPath}.firstName" cssClass="error-description"/>
                            </div>

                            <h2>Addresses:</h2>
                            <c:set var="containerId" value="${personPath}.addresses"/>
                            <c:set var="containerIdReal" value="${guardianItemId}.person.addresses"/>
                            <div id="${containerIdReal}">
                                <c:forEach var="address" items="${person.addresses}" varStatus="status">
                                    <c:set var="subContainerId" value="${containerIdReal}${status.index}Field"/>
                                    <div class="field" id="${subContainerId}" data-index="${status.index}">
                                            <%--<form:hidden path="${personPath}.addresses[${status.index}].id"/>--%>

                                        <form:select path="${containerId}[${status.index}].addressType"
                                                     items="${addressTypeEnum}"/>
                                        <button class="negative" type="button"
                                                onclick="ivis.ui.removeContainer('${subContainerId}');">Remove
                                        </button>
                                            <%--<input type="hidden" name="${containerId}[${status.index}].addressType" value="${address.addressType}"/>--%>

                                        <form:label path="${containerId}[${status.index}].careOf">c/o</form:label>
                                        <form:input data-rule-maxlength="255"
                                                    path="${containerId}[${status.index}].careOf"
                                                    cssErrorClass="error"/>
                                        <form:errors path="${containerId}[${status.index}].careOf"
                                                     cssClass="error-description"/>

                                        <form:label path="${containerId}[${status.index}].street">Street</form:label>
                                        <form:input data-rule-maxlength="255"
                                                    path="${containerId}[${status.index}].street"
                                                    cssErrorClass="error"/>
                                        <form:errors path="${containerId}[${status.index}].street"
                                                     cssClass="error-description"/>

                                        <form:label
                                                path="${containerId}[${status.index}].postalCode">Postal code</form:label>
                                        <form:input data-rule-maxlength="255" data-rule-digits="true"
                                                    path="${containerId}[${status.index}].postalCode"
                                                    cssErrorClass="error"/>
                                        <form:errors path="${containerId}[${status.index}].postalCode"
                                                     cssClass="error-description"/>

                                        <form:label path="${containerId}[${status.index}].city">City</form:label>
                                        <form:input data-rule-maxlength="255"
                                                    path="${containerId}[${status.index}].city" cssErrorClass="error"/>
                                        <form:errors path="${containerId}[${status.index}].city"
                                                     cssClass="error-description"/>

                                        <form:label
                                                path="${containerId}[${status.index}].municipalityCode">Municipality code</form:label>
                                        <form:input data-rule-maxlength="255"
                                                    path="${containerId}[${status.index}].municipalityCode"
                                                    cssErrorClass="error"/>
                                        <form:errors path="${containerId}[${status.index}].municipalityCode"
                                                     cssClass="error-description"/>
                                    </div>
                                </c:forEach>
                                <button class="positive" type="button" onclick="ivis.ui.addAddress('${containerId}');">
                                    Add
                                </button>
                            </div>

                            <h2>Phones:</h2>
                            <c:set var="containerId" value="${personPath}.phones"/>
                            <c:set var="containerIdReal" value="${guardianItemId}.person.phones"/>
                            <div id="${containerIdReal}">
                                <c:forEach var="phone" items="${person.phones}" varStatus="status">
                                    <c:set var="subContainerId" value="${containerIdReal}${status.index}Field"/>
                                    <div class="field" id="${subContainerId}" data-index="${status.index}">
                                            <%--<form:hidden path="${personPath}.phones[${status.index}].id"/>--%>

                                            <%--<input type="hidden" name="${personPath}.phones[${status.index}].communicationType" value="${phone.communicationType}"/>--%>
                                        <form:select path="${containerId}[${status.index}].communicationType"
                                                     items="${communicationTypeEnum}"/>
                                        <button class="negative" type="button"
                                                onclick="ivis.ui.removeContainer('${subContainerId}');">Remove
                                        </button>
                                        <form:label path="${containerId}[${status.index}].number">Phone</form:label>
                                        <form:input path="${containerId}[${status.index}].number"
                                                    cssErrorClass="error"
                                                    data-rule-pattern="\s*((\+([\s-]*\d[\s-]*){2}|([\s-]*\d[\s-]*)?)?((\(([\s-]*\d[\s-]*){3}\))|([\s-]*\d[\s-]*){3}))?([\s-]*\d[\s-]*){7}"/>
                                        <form:errors path="${containerId}[${status.index}].number"
                                                     cssClass="error-description"/>
                                    </div>
                                </c:forEach>
                                <button class="positive" type="button" onclick="ivis.ui.addPhone('${containerId}');">Add
                                </button>
                            </div>

                            <h2>Emails:</h2>
                            <c:set var="containerId" value="${personPath}.emails"/>
                            <c:set var="containerIdReal" value="${guardianItemId}.person.emails"/>
                            <div id="${containerIdReal}">
                                <c:forEach var="email" items="${person.emails}" varStatus="status">
                                    <c:set var="subContainerId" value="${containerIdReal}${status.index}Field"/>
                                    <div class="field" id="${subContainerId}" data-index="${status.index}">
                                            <%--<form:hidden path="person.${containerId}[${status.index}].id"/>--%>

                                        <form:select path="${containerId}[${status.index}].communicationType"
                                                     items="${communicationTypeEnum}"/>
                                        <button class="negative" type="button"
                                                onclick="ivis.ui.removeContainer('${subContainerId}');">Remove
                                        </button>
                                        <form:label
                                                path="${containerId}[${status.index}].address">Email</form:label>
                                        <form:input data-rule-email="true"
                                                    path="${containerId}[${status.index}].address"
                                                    cssErrorClass="error"/>
                                        <form:errors path="${containerId}[${status.index}].address"
                                                     cssClass="error-description"/>
                                    </div>
                                </c:forEach>
                                <button class="positive" type="button" onclick="ivis.ui.addEmail('${containerId}');">Add
                                </button>
                            </div>
                        </div>
                    </div>
                </c:forEach>
                <div class="buttons" id="addGuardianButton">
                    <button class="positive" type="button" onclick="ivisOAuth('${clientAddress}/guardians_list.jsp');">
                        Add guardian
                    </button>
                </div>
            </div>
            <c:set var="hasContactPerson" value="${not empty pupil.contactPerson}"/>
            <div class="checkbox">
                <input id="hasContactPerson" type="checkbox" ${hasContactPerson ? 'checked': ''}
                       onchange="ivis.ui.disableContactPerson('hasContactPerson', 'pupil.contactPersonField');">
                <label for="hasContactPerson">Contact person</label>
            </div>
            <div id="pupil.contactPersonField">
                <c:if test="${hasContactPerson}">
                    <c:set var="personPath" value="contactPerson"/>
                    <c:set var="person" value="${pupil.contactPerson}"/>
                    <input id="person" type="hidden" name="${personPath}.id" value="${person.id}">

                    <div class="field">
                        <form:label path="${personPath}.personalId">Personal ID</form:label>
                        <form:input data-rule-maxlength="255" path="${personPath}.personalId" cssErrorClass="error"/>
                        <form:errors path="${personPath}.personalId" cssClass="error-description"/>
                    </div>
                    <div class="field">
                        <form:label path="${personPath}.lastName">Last name</form:label>
                        <form:input data-rule-maxlength="255" path="${personPath}.lastName" cssErrorClass="error"/>
                        <form:errors path="${personPath}.lastName" cssClass="error-description"/>
                    </div>
                    <div class="field">
                        <form:label path="${personPath}.firstName">
                            First name
                        </form:label>
                        <form:input data-rule-maxlength="255" path="${personPath}.firstName" cssErrorClass="error"/>
                        <form:errors path="${personPath}.firstName" cssClass="error-description"/>
                    </div>

                    <h2>Addresses:</h2>
                    <c:set var="containerId" value="${personPath}.addresses"/>
                    <div id="${containerId}">
                        <c:forEach var="address" items="${person.addresses}" varStatus="status">
                            <c:set var="subContainerId" value="${containerId}${status.index}Field"/>
                            <div class="field" id="${subContainerId}" data-index="${status.index}">
                                    <%--<form:hidden path="${personPath}.addresses[${status.index}].id"/>--%>

                                <form:select path="${containerId}[${status.index}].addressType"
                                             items="${addressTypeEnum}"/>
                                <button class="negative" type="button"
                                        onclick="ivis.ui.removeContainer('${subContainerId}');">Remove
                                </button>
                                    <%--<input type="hidden" name="${containerId}[${status.index}].addressType" value="${address.addressType}"/>--%>

                                <form:label path="${containerId}[${status.index}].careOf">c/o</form:label>
                                <form:input data-rule-maxlength="255" path="${containerId}[${status.index}].careOf"
                                            cssErrorClass="error"/>
                                <form:errors path="${containerId}[${status.index}].careOf"
                                             cssClass="error-description"/>

                                <form:label path="${containerId}[${status.index}].street">Street</form:label>
                                <form:input data-rule-maxlength="255" path="${containerId}[${status.index}].street"
                                            cssErrorClass="error"/>
                                <form:errors path="${containerId}[${status.index}].street"
                                             cssClass="error-description"/>

                                <form:label path="${containerId}[${status.index}].postalCode">Postal code</form:label>
                                <form:input data-rule-maxlength="255" data-rule-digits="true"
                                            path="${containerId}[${status.index}].postalCode" cssErrorClass="error"/>
                                <form:errors path="${containerId}[${status.index}].postalCode"
                                             cssClass="error-description"/>

                                <form:label path="${containerId}[${status.index}].city">City</form:label>
                                <form:input data-rule-maxlength="255" path="${containerId}[${status.index}].city"
                                            cssErrorClass="error"/>
                                <form:errors path="${containerId}[${status.index}].city" cssClass="error-description"/>

                                <form:label
                                        path="${containerId}[${status.index}].municipalityCode">Municipality code</form:label>
                                <form:input data-rule-maxlength="255"
                                            path="${containerId}[${status.index}].municipalityCode"
                                            cssErrorClass="error"/>
                                <form:errors path="${containerId}[${status.index}].municipalityCode"
                                             cssClass="error-description"/>
                            </div>
                        </c:forEach>
                        <button class="positive" type="button" onclick="ivis.ui.addAddress('${containerId}');">Add
                        </button>
                    </div>

                    <h2>Phones:</h2>
                    <c:set var="containerId" value="${personPath}.phones"/>
                    <div id="${containerId}">
                        <c:forEach var="phone" items="${person.phones}" varStatus="status">
                            <c:set var="subContainerId" value="${containerId}${status.index}Field"/>
                            <div class="field" id="${subContainerId}" data-index="${status.index}">
                                    <%--<form:hidden path="${personPath}.phones[${status.index}].id"/>--%>

                                    <%--<input type="hidden" name="${personPath}.phones[${status.index}].communicationType" value="${phone.communicationType}"/>--%>
                                <form:select path="${containerId}[${status.index}].communicationType"
                                             items="${communicationTypeEnum}"/>
                                <button class="negative" type="button"
                                        onclick="ivis.ui.removeContainer('${subContainerId}');">Remove
                                </button>
                                <form:label path="${containerId}[${status.index}].number">Phone</form:label>
                                <form:input path="${containerId}[${status.index}].number" cssErrorClass="error"
                                            data-rule-pattern="\s*((\+([\s-]*\d[\s-]*){2}|([\s-]*\d[\s-]*)?)?((\(([\s-]*\d[\s-]*){3}\))|([\s-]*\d[\s-]*){3}))?([\s-]*\d[\s-]*){7}"/>
                                <form:errors path="${containerId}[${status.index}].number"
                                             cssClass="error-description"/>
                            </div>
                        </c:forEach>
                        <button class="positive" type="button" onclick="ivis.ui.addPhone('${containerId}');">Add
                        </button>
                    </div>

                    <h2>Emails:</h2>
                    <c:set var="containerId" value="${personPath}.emails"/>
                    <div id="${containerId}">
                        <c:forEach var="email" items="${person.emails}" varStatus="status">
                            <c:set var="subContainerId" value="${containerId}${status.index}Field"/>
                            <div class="field" id="${subContainerId}" data-index="${status.index}">
                                    <%--<form:hidden path="person.${containerId}[${status.index}].id"/>--%>

                                <form:select path="${containerId}[${status.index}].communicationType"
                                             items="${communicationTypeEnum}"/>
                                <button class="negative" type="button"
                                        onclick="ivis.ui.removeContainer('${subContainerId}');">Remove
                                </button>
                                <form:label
                                        path="${containerId}[${status.index}].address">Email</form:label>
                                <form:input data-rule-email="true" path="${containerId}[${status.index}].address"
                                            cssErrorClass="error"/>
                                <form:errors path="${containerId}[${status.index}].address"
                                             cssClass="error-description"/>
                            </div>
                        </c:forEach>
                        <button class="positive" type="button" onclick="ivis.ui.addEmail('${containerId}');">Add
                        </button>
                    </div>
                </c:if>
            </div>
            <div class="buttons">
                <button class="positive" type="submit">Save</button>
                <a class="button neutral" type="/pupils">Cancel</a>
            </div>
        </div>
        <div id="schoolAndClassTabPage" class="tab-page">
            <div class="field">
                <form:select path="school" items="${schoolList}" itemValue="id" itemLabel="name"
                             onchange="ivis.ui.onSchoolChange(this.value);"/>
                <label>School ID</label>
                <label id="schoolIdLabel">${pupil.school.schoolId}</label>
            </div>
            <div class="field">
                <form:select path="academicYear" items="${academicYearList}" itemLabel="name" itemValue="id"/>
            </div>
            <div class="field">
                <form:select path="schoolClass" items="${schoolClassList}" itemLabel="name" itemValue="id"/>
            </div>

            <div class="field">
                <form:label path="classPlacementFrom">Class placement from</form:label>
                <fmt:formatDate value="${pupil.classPlacementFrom}" var="dateString" pattern="yyyy-MM-dd"/>
                <input id="classPlacementFrom" name="classPlacementFrom" type="date" value="">
                    <%--<input id="classPlacementFrom" name="classPlacementFrom" type="date" value="${dateString}">--%>
                    <%--<form:input path="classPlacementFrom" cssErrorClass="error" type="date"/>--%>
                <form:errors path="classPlacementFrom" cssClass="error-description"/>
                <form:label path="classPlacementTo">to</form:label>
                    <%--<form:input path="classPlacementTo" cssErrorClass="error" type="date"/>--%>
                <fmt:formatDate value="${pupil.classPlacementTo}" var="dateString" pattern="yyyy-MM-dd"/>
                <input id="classPlacementTo" name="classPlacementTo" type="date" value="${dateString}">
                <form:errors path="classPlacementTo" cssClass="error-description"/>
            </div>

            <h2>School days, start and end time</h2>

            <c:if test="${not empty pupil.schoolClass and not empty pupil.schoolClass.diaries}">
                <table cellpadding="0" cellspacing="0" class="field">
                    <thead>
                    <tr>
                        <th>&nbsp;</th>
                        <th>Start</th>
                        <th>End</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="afterSchoolSchema" items="${pupil.schoolClass.diaries}">
                        <tr>
                            <td>${afterSchoolSchema.dayOfWeek}</td>
                            <td><fmt:formatDate value="${afterSchoolSchema.startTime}" type="time" pattern="HH:mm"/></td>
                            <td><fmt:formatDate value="${afterSchoolSchema.endTime}" type="both" pattern="HH:mm"/></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>

            <div class="buttons">
                <button class="positive" type="submit">Save</button>
                <a class="button neutral" type="/pupils">Cancel</a>
            </div>
        </div>
        <div id="schoolTransportTabPage" class="tab-page">
            <c:if test="${not empty applicationList}">
                <h1>School transport applications</h1>
                <table cellpadding="0" cellspacing="0">
                    <thead>
                    <tr>
                        <th class="ordered-by">Id</th>
                        <th>SubmitDate</th>
                        <th>ChangeDate</th>
                        <th>Status</th>
                        <th>Handled by</th>
                        <th>&nbsp;</th>
                    </tr>
                    </thead>

                    <tbody>
                    <c:forEach items="${applicationList}" var="app">
                        <tr data-application-id="${app.id}">
                            <td>${app.id}</td>
                            <td>${app.submitDate}</td>
                            <td>${app.changedDate}</td>
                            <td>${app.status}</td>
                            <td>${app.submittedPerson}</td>
                            <td class="buttons">
                                <a class="button positive"
                                   href="<%=Imcms.getServerProperties().getProperty("ClientAddress")%>/details.jsp?id=${app.id}">Details</a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>

                </table>
            </c:if>
            <div class="buttons">
                <button class="positive" type="submit">Save</button>
                <a class="button neutral" type="/pupils">Cancel</a>
            </div>
        </div>
        <div id="loggTabPage" class="tab-page">

        </div>
    </form:form>
</c:if>
<script type="text/javascript">
    <%--var communicationTypeEnum = [<%--%>
    <%--Enum[] values = CommunicationTypeEnum.values();--%>

    <%--for(int i = 0; i < values.length; i++) {--%>
    <%--Enum typeEnum = values[i];--%>
    <%--out.print("{name:\"");--%>
    <%--out.print(typeEnum.name());--%>
    <%--out.print("\", description:\"");--%>
    <%--out.print(typeEnum.toString());--%>
    <%--out.print("\"}, ");--%>
    <%--}--%>
    <%--%>];--%>

    <%--var addressTypeEnum = [<%--%>
    <%--values = AddressTypeEnum.values();--%>

    <%--for(int i = 0; i < values.length; i++) {--%>
    <%--Enum typeEnum = values[i];--%>
    <%--out.print("{name:\"");--%>
    <%--out.print(typeEnum.name());--%>
    <%--out.print("\", description:\"");--%>
    <%--out.print(typeEnum.toString());--%>
    <%--out.print("\"}, ");--%>
    <%--}--%>
    <%--%>];--%>
    $(document).ready(function () {
//        alert("asdfasdf");
        $('#application-form').validate();
    });
</script>
<%--<c:if test="${not empty statement}">--%>
<%--<table cellpadding="0" cellspacing="0">--%>
<%--<tbody>--%>
<%--<tr>--%>
<%--<td>Id</td>--%>
<%--<td>${statement.id}</td>--%>
<%--</tr>--%>
<%--<tr>--%>
<%--<td>Submit date</td>--%>
<%--<td>${statement.submitDate}</td>--%>
<%--</tr>--%>
<%--<tr>--%>
<%--<td>Change date</td>--%>
<%--<td>${statement.changedDate}</td>--%>
<%--</tr>--%>
<%--<tr>--%>
<%--<td>Status</td>--%>
<%--<td>${statement.status}</td>--%>
<%--</tr>--%>
<%--<tr>--%>
<%--<td>Submitted person</td>--%>
<%--<td>${statement.submittedPerson}</td>--%>
<%--</tr>--%>
<%--<tr>--%>
<%--<td>Pupil</td>--%>
<%--<td>${statement.pupil}</td>--%>
<%--</tr>--%>
<%--</tbody>--%>
<%--</table>--%>
<%--<div class="buttons">--%>
<%--<a class="button neutral" href="<%=Imcms.getServerProperties().getProperty("StatementsAddress")%>">Back</a>--%>
<%--</div>--%>
<%--</c:if>--%>
<jsp:include page="ivis_footer.jsp"/>