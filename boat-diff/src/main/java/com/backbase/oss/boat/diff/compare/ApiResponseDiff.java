package com.backbase.oss.boat.diff.compare;

import com.backbase.oss.boat.diff.model.ChangedApiResponse;
import com.backbase.oss.boat.diff.model.ChangedResponse;
import com.backbase.oss.boat.diff.model.DiffContext;
import com.backbase.oss.boat.diff.utils.ChangedUtils;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by adarsh.sharma on 04/01/18.
 */
public class ApiResponseDiff {
    private OpenApiDiff openApiDiff;

    public ApiResponseDiff(OpenApiDiff openApiDiff) {
        this.openApiDiff = openApiDiff;
    }

    public Optional<ChangedApiResponse> diff(
            ApiResponses left, ApiResponses right, DiffContext context) {
        MapKeyDiff<String, ApiResponse> responseMapKeyDiff = MapKeyDiff.diff(left, right);
        List<String> sharedResponseCodes = responseMapKeyDiff.getSharedKey();
        Map<String, ChangedResponse> resps = new LinkedHashMap<>();
        for (String responseCode : sharedResponseCodes) {
            openApiDiff
                    .getResponseDiff()
                    .diff(left.get(responseCode), right.get(responseCode), context)
                    .ifPresent(changedResponse -> resps.put(responseCode, changedResponse));
        }
        ChangedApiResponse changedApiResponse =
                new ChangedApiResponse(left, right, context)
                        .setIncreased(responseMapKeyDiff.getIncreased())
                        .setMissing(responseMapKeyDiff.getMissing())
                        .setChanged(resps);
        openApiDiff
                .getExtensionsDiff()
                .diff(left.getExtensions(), right.getExtensions(), context)
                .ifPresent(changedApiResponse::setExtensions);
        return ChangedUtils.isChanged(changedApiResponse);
    }
}
