package com.backbase.oss.boat.diff.output;

import static j2html.TagCreator.body;
import static j2html.TagCreator.del;
import static j2html.TagCreator.div;
import static j2html.TagCreator.document;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.h3;
import static j2html.TagCreator.head;
import static j2html.TagCreator.header;
import static j2html.TagCreator.hr;
import static j2html.TagCreator.html;
import static j2html.TagCreator.li;
import static j2html.TagCreator.link;
import static j2html.TagCreator.meta;
import static j2html.TagCreator.ol;
import static j2html.TagCreator.p;
import static j2html.TagCreator.script;
import static j2html.TagCreator.span;
import static j2html.TagCreator.title;
import static j2html.TagCreator.ul;

import com.backbase.oss.boat.diff.model.Changed;
import com.backbase.oss.boat.diff.model.ChangedApiResponse;
import com.backbase.oss.boat.diff.model.ChangedContent;
import com.backbase.oss.boat.diff.model.ChangedMediaType;
import com.backbase.oss.boat.diff.model.ChangedMetadata;
import com.backbase.oss.boat.diff.model.ChangedOpenApi;
import com.backbase.oss.boat.diff.model.ChangedOperation;
import com.backbase.oss.boat.diff.model.ChangedParameter;
import com.backbase.oss.boat.diff.model.ChangedParameters;
import com.backbase.oss.boat.diff.model.ChangedResponse;
import com.backbase.oss.boat.diff.model.ChangedSchema;
import com.backbase.oss.boat.diff.model.ChangedSecurityRequirement;
import com.backbase.oss.boat.diff.model.ChangedSecurityRequirements;
import com.backbase.oss.boat.diff.model.DiffContext;
import com.backbase.oss.boat.diff.model.DiffResult;
import com.backbase.oss.boat.diff.model.Endpoint;
import com.backbase.oss.boat.diff.utils.RefPointer;
import com.backbase.oss.boat.diff.utils.RefType;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import j2html.tags.ContainerTag;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"rawtypes", "java:S100", "java:S117", "java:S3740"})
public class HtmlRender implements Render {

    public static final String BOOSTRAP_CSS = "https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css";
    public static final String BOOTSTRAP_THEME_CSS = "https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap-theme.min.css";
    public static final String BOOTSTRAP_CSS_SHA = "sha384-HSMxcRTRxnN+Bdg0JdbxYKrThecOKuH5zCYotlSAcp1+c8xmyTe9GYg1l9a69psu";
    public static final String BOOTSTRAP_CSS_THEME_SHA = "sha384-6pzBo3FDv/PJ8r2KRkGHifhEocL+1X2rVCTTkUfGk7/0pbek5mMa1upzvWbrUbOZ";
    public static final String BOOTSTRAP_JS_SHA = "sha384-aJ21OjlMXNL5UyIl/XNwTMqvzeRMZH2w8c5cRVpzpU8Y5bApTppSuUkhZXN0VxHd";
    public static final String BOOSTRAP_JS = "https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js";
    public static final String CROSSORIGIN = "crossorigin";
    public static final String ANONYMOUS = "anonymous";
    public static final String INTEGRITY = "integrity";
    public static final String STYLESHEET = "stylesheet";
    public static final String COMMENT = "comment";
    public static final String MISSING = "missing";
    private final String title;
    protected static RefPointer<Schema> refPointer = new RefPointer<>(RefType.SCHEMAS);
    protected ChangedOpenApi diff;

    public HtmlRender() {
        this("Api Change Log");
    }

    public HtmlRender(String title) {
        this.title = title;

    }

    public String render(ChangedOpenApi diff) {
        this.diff = diff;

        List<Endpoint> newEndpoints = diff.getNewEndpoints();
        ContainerTag ol_newEndpoint = ol_newEndpoint(newEndpoints);

        List<Endpoint> missingEndpoints = diff.getMissingEndpoints();
        ContainerTag ol_missingEndpoint = ol_missingEndpoint(missingEndpoints);

        List<Endpoint> deprecatedEndpoints = diff.getDeprecatedEndpoints();
        ContainerTag ol_deprecatedEndpoint = ol_deprecatedEndpoint(deprecatedEndpoints);

        List<ChangedOperation> changedOperations = diff.getChangedOperations();
        ContainerTag ol_changed = ol_changed(changedOperations);

        return renderHtml(ol_newEndpoint, ol_missingEndpoint, ol_deprecatedEndpoint, ol_changed);
    }

    public String renderHtml(
        ContainerTag ol_new, ContainerTag ol_miss, ContainerTag ol_deprec, ContainerTag ol_changed) {
        ContainerTag html =
            html()
                .attr("lang", "en")
                .with(
                    head()
                        .with(
                            meta().withCharset("utf-8"),
                            title(title),
                            link().withRel(STYLESHEET).withHref(BOOSTRAP_CSS)
                                .attr(CROSSORIGIN, ANONYMOUS)
                                .attr(INTEGRITY, BOOTSTRAP_CSS_SHA),
                            link().withRel(STYLESHEET).withHref(BOOTSTRAP_THEME_CSS)
                                .attr(CROSSORIGIN, ANONYMOUS)
                                .attr(INTEGRITY, BOOTSTRAP_CSS_THEME_SHA),
                            script().withSrc(BOOSTRAP_JS)
                                .attr(INTEGRITY, BOOTSTRAP_JS_SHA)
                                .attr(CROSSORIGIN, ANONYMOUS)
                        ),

                    body()
                        .with(
                            header().with(h1(title)),
                            div()
                                .withClass("article")
                                .with(
                                    div().with(h2("What's New"), hr(), ol_new),
                                    div().with(h2("What's Deleted"), hr(), ol_miss),
                                    div().with(h2("What's Deprecated"), hr(), ol_deprec),
                                    div().with(h2("What's Changed"), hr(), ol_changed))));

        return document().render() + html.render();
    }

    private ContainerTag ol_newEndpoint(List<Endpoint> endpoints) {
        if (null == endpoints) {
            return ol();
        }
        ContainerTag ol = ol();
        for (Endpoint endpoint : endpoints) {
            ol.with(
                li_newEndpoint(
                    endpoint.getMethod().toString(), endpoint.getPathUrl(), endpoint.getSummary()));
        }
        return ol;
    }

    private ContainerTag li_newEndpoint(String method, String path, String desc) {
        return li().with(span(method).withClass(method)).withText(path + " ").with(span(desc));
    }

    private ContainerTag ol_missingEndpoint(List<Endpoint> endpoints) {
        if (null == endpoints) {
            return ol();
        }
        ContainerTag ol = ol();
        for (Endpoint endpoint : endpoints) {
            ol.with(
                li_missingEndpoint(
                    endpoint.getMethod().toString(), endpoint.getPathUrl(), endpoint.getSummary()));
        }
        return ol;
    }

    private ContainerTag li_missingEndpoint(String method, String path, String desc) {
        return li().with(span(method).withClass(method), del().withText(path)).with(span(" " + desc));
    }

    private ContainerTag ol_deprecatedEndpoint(List<Endpoint> endpoints) {
        if (null == endpoints) {
            return ol();
        }
        ContainerTag ol = ol();
        for (Endpoint endpoint : endpoints) {
            ol.with(
                li_deprecatedEndpoint(
                    endpoint.getMethod().toString(), endpoint.getPathUrl(), endpoint.getSummary()));
        }
        return ol;
    }

    private ContainerTag li_deprecatedEndpoint(String method, String path, String desc) {
        return li().with(span(method).withClass(method), del().withText(path)).with(span(" " + desc));
    }

    private ContainerTag ol_changed(List<ChangedOperation> changedOperations) {
        if (null == changedOperations) {
            return ol();
        }
        ContainerTag ol = ol();
        for (ChangedOperation changedOperation : changedOperations) {
            String pathUrl = changedOperation.getPathUrl();
            String method = changedOperation.getHttpMethod().toString();
            String desc =
                Optional.ofNullable(changedOperation.getSummary())
                    .map(ChangedMetadata::getRight)
                    .orElse("");

            ContainerTag ul_detail = ul().withClass("detail");
            if (Changed.result(changedOperation.getParameters()).isDifferent()) {
                ul_detail.with(
                    li().with(h3("Parameters")).with(ul_param(changedOperation.getParameters())));
            }
            if (changedOperation.resultRequestBody().isDifferent()) {
                ul_detail.with(
                    li().with(h3("Request"))
                        .with(ul_request(changedOperation.getRequestBody().getContent())));
            }
            if (changedOperation.resultApiResponses().isDifferent()) {
                ul_detail.with(
                    li().with(h3("Response")).with(ul_response(changedOperation.getApiResponses())));
            }

            if (changedOperation.getSecurityRequirements() != null && changedOperation.getSecurityRequirements()
                .isDifferent()) {
                ul_detail.with(
                    li().with(h3("Security")).with(ul_security(changedOperation.getSecurityRequirements())));
            }

            ol.with(
                li().with(span(method).withClass(method))
                    .withText(pathUrl + " ")
                    .with(span(desc))
                    .with(ul_detail));
        }
        return ol;
    }

    private ContainerTag ul_security(ChangedSecurityRequirements apiSecurityReq) {
        List<ChangedSecurityRequirement> changedSecurity = apiSecurityReq.getChanged();

        ContainerTag ul = ul().withClass("change security");

        if (changedSecurity != null) {
            changedSecurity.forEach(
                e -> ul.with(li_addSecurity(e))
            );
        }
        return ul;

    }

    private ContainerTag ul_response(ChangedApiResponse changedApiResponse) {
        Map<String, ApiResponse> addResponses = changedApiResponse.getIncreased();
        Map<String, ApiResponse> delResponses = changedApiResponse.getMissing();
        Map<String, ChangedResponse> changedResponses = changedApiResponse.getChanged();
        ContainerTag ul = ul().withClass("change response");
        addResponses.keySet().stream().map(propName -> li_addResponse(propName, addResponses.get(propName)))
            .forEach(ul::with);
        delResponses.keySet().stream().map(propName -> li_missingResponse(propName, delResponses.get(propName)))
            .forEach(ul::with);
        changedResponses.keySet().stream().map(propName -> li_changedResponse(propName, changedResponses.get(propName)))
            .forEach(ul::with);
        return ul;
    }

    private ContainerTag li_addSecurity(ChangedSecurityRequirement requirement) {
        return li(div(String.format("changed security : New -> [%s]", requirement.getNewSecurityRequirement()))
            .withClass(COMMENT))
            .with(div(("changed security : Old -> " + requirement.getOldSecurityRequirement())).withClass(MISSING));
    }

    private ContainerTag li_addResponse(String name, ApiResponse response) {
        return li().withText(String.format("New response : [%s]", name))
            .with(
                span(null == response.getDescription() ? "" : ("//" + response.getDescription()))
                    .withClass(COMMENT));
    }

    private ContainerTag li_missingResponse(String name, ApiResponse response) {
        return li().withText(String.format("Deleted response : [%s]", name))
            .with(
                span(null == response.getDescription() ? "" : ("//" + response.getDescription()))
                    .withClass(COMMENT));
    }

    private ContainerTag li_changedResponse(String name, ChangedResponse response) {
        return li().withText(String.format("Changed response : [%s]", name))
            .with(
                span((null == response.getNewApiResponse()
                    || null == response.getNewApiResponse().getDescription())
                    ? ""
                    : ("//" + response.getNewApiResponse().getDescription()))
                    .withClass(COMMENT))
            .with(ul_request(response.getContent()));
    }

    private ContainerTag ul_request(ChangedContent changedContent) {
        ContainerTag ul = ul().withClass("change request-body");
        if (changedContent != null) {
            for (String propName : changedContent.getIncreased().keySet()) {
                ul.with(li_addRequest(propName, changedContent.getIncreased().get(propName)));
            }
            for (String propName : changedContent.getMissing().keySet()) {
                ul.with(li_missingRequest(propName, changedContent.getMissing().get(propName)));
            }
            for (String propName : changedContent.getChanged().keySet()) {
                ul.with(li_changedRequest(propName, changedContent.getChanged().get(propName)));
            }
        }
        return ul;
    }

    @SuppressWarnings("java:S1172")
    private ContainerTag li_addRequest(String name, MediaType request) {
        return li().withText(String.format("New body: '%s'", name));
    }

    @SuppressWarnings("java:S1172")
    private ContainerTag li_missingRequest(String name, MediaType request) {
        return li().withText(String.format("Deleted body: '%s'", name));
    }

    private ContainerTag li_changedRequest(String name, ChangedMediaType request) {
        ContainerTag li =
            li().with(div_changedSchema(request.getSchema()))
                .withText(String.format("Changed body: '%s'", name));
        if (request.isIncompatible()) {
            incompatibilities(li, request.getSchema());
        }
        return li;
    }

    private ContainerTag div_changedSchema(ChangedSchema schema) {
        ContainerTag div = div();
        div.with(h3("Schema" + (schema.isIncompatible() ? " incompatible" : "")));
        return div;
    }

    private void incompatibilities(final ContainerTag output, final ChangedSchema schema) {
        incompatibilities(output, "", schema);
    }

    private void incompatibilities(
        final ContainerTag output, String propName, final ChangedSchema schema) {
        if (schema.getItems() != null) {
            items(output, propName, schema.getItems());
        }
        if (schema.isCoreChanged() == DiffResult.INCOMPATIBLE && schema.isChangedType()) {
            String type = type(schema.getOldSchema()) + " -> " + type(schema.getNewSchema());
            property(output, propName, "Changed property type", type);
        }
        String prefix = propName.isEmpty() ? "" : propName + ".";
        properties(
            output, prefix, "Missing property", schema.getMissingProperties(), schema.getContext());
        schema
            .getChangedProperties()
            .forEach((name, property) -> incompatibilities(output, prefix + name, property));
    }

    private void items(ContainerTag output, String propName, ChangedSchema schema) {
        incompatibilities(output, propName + "[n]", schema);
    }

    @SuppressWarnings("java:S1172")
    private void properties(
        ContainerTag output,
        String propPrefix,
        String title,
        Map<String, Schema> properties,
        DiffContext context) {
        if (properties != null) {
            properties.forEach((key, value) -> property(output, propPrefix + key, title, resolve(value)));
        }
    }

    protected void property(ContainerTag output, String name, String title, Schema schema) {
        property(output, name, title, type(schema));
    }

    protected void property(ContainerTag output, String name, String title, String type) {
        output.with(p(String.format("%s: %s (%s)", title, name, type)).withClass(MISSING));
    }

    protected Schema resolve(Schema schema) {
        return refPointer.resolveRef(
            diff.getNewSpecOpenApi().getComponents(), schema, schema.get$ref());
    }

    protected String type(Schema schema) {
        String result = "object";
        if (schema instanceof ArraySchema) {
            result = "array";
        } else if (schema.getType() != null) {
            result = schema.getType();
        }
        return result;
    }

    private ContainerTag ul_param(ChangedParameters changedParameters) {
        List<Parameter> addParameters = changedParameters.getIncreased();
        List<Parameter> delParameters = changedParameters.getMissing();
        List<ChangedParameter> changed = changedParameters.getChanged();
        ContainerTag ul = ul().withClass("change param");
        for (Parameter param : addParameters) {
            ul.with(li_addParam(param));
        }
        for (ChangedParameter param : changed) {
            ul.with(li_changedParam(param));
        }
        for (Parameter param : delParameters) {
            ul.with(li_missingParam(param));
        }
        return ul;
    }

    private ContainerTag li_addParam(Parameter param) {
        return li().withText("Add " + param.getName() + " in " + param.getIn())
            .with(
                span(null == param.getDescription() ? "" : ("//" + param.getDescription()))
                    .withClass(COMMENT));
    }

    private ContainerTag li_missingParam(Parameter param) {
        return li().withClass(MISSING)
            .with(span("Delete"))
            .with(del(param.getName()))
            .with(span("in ").withText(param.getIn()))
            .with(
                span(null == param.getDescription() ? "" : ("//" + param.getDescription()))
                    .withClass(COMMENT));
    }

    private ContainerTag li_deprecatedParam(ChangedParameter param) {
        return li().withClass(MISSING)
            .with(span("Deprecated"))
            .with(del(param.getName()))
            .with(span("in ").withText(param.getIn()))
            .with(
                span(null == param.getNewParameter().getDescription()
                    ? ""
                    : ("//" + param.getNewParameter().getDescription()))
                    .withClass(COMMENT));
    }

    private ContainerTag li_changedParam(ChangedParameter changeParam) {
        if (changeParam.isDeprecated()) {
            return li_deprecatedParam(changeParam);
        }
        boolean changeRequired = changeParam.isChangeRequired();
        ChangedMetadata description = changeParam.getDescription();
        boolean changeDescription = description != null && description.isDifferent();
        Parameter rightParam = changeParam.getNewParameter();
        Parameter leftParam = changeParam.getNewParameter();
        ContainerTag li = li().withText(changeParam.getName() + " in " + changeParam.getIn());
        if (changeRequired) {
            li.withText(" change into " + (rightParam.getRequired().booleanValue() ? "required" : "not required"));
        }
        if (changeDescription) {
            li.withText(" Notes ")
                .with(del(leftParam.getDescription()).withClass(COMMENT))
                .withText(" change into ")
                .with(span(rightParam.getDescription()).withClass(COMMENT));
        }
        return li;
    }
}
