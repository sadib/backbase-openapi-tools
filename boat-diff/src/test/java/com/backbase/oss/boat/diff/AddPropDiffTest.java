package com.backbase.oss.boat.diff;

import static com.backbase.oss.boat.diff.TestUtils.assertOpenApiAreEquals;
import static com.backbase.oss.boat.diff.TestUtils.assertOpenApiBackwardIncompatible;

import org.junit.jupiter.api.Test;

/**
 * Created by adarsh.sharma on 26/02/18.
 */
public class AddPropDiffTest {
    private final String OPENAPI_DOC1 = "add-prop-1.yaml";
    private final String OPENAPI_DOC2 = "add-prop-2.yaml";

    @Test
    public void testDiffSame() {
        assertOpenApiAreEquals(OPENAPI_DOC1, OPENAPI_DOC1);
    }

    @Test
    public void testDiffDifferent() {
        assertOpenApiBackwardIncompatible(OPENAPI_DOC1, OPENAPI_DOC2);
    }
}
