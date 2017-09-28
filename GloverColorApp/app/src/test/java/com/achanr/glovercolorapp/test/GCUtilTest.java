package com.achanr.glovercolorapp.test;

import android.content.Context;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.common.GCUtil;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

/**
 * @author Andrew Chanrasmi on 9/27/17
 */
@RunWith(MockitoJUnitRunner.class)
public class GCUtilTest {

    @Mock
    Context mMockContext;

    @Test
    public void testConvertToTitleCase_usingNullValue_returnsEmptyString() throws Exception {
        String convertedString = GCUtil.convertToTitleCase(mMockContext, null);
        Assert.assertEquals("", convertedString);
    }

    @Test
    public void testConvertToTitleCase_usingEmptyValue_returnsEmptyString() throws Exception {
        String convertedString = GCUtil.convertToTitleCase(mMockContext, "");
        Assert.assertEquals("", convertedString);
    }

    @Test
    public void testConvertToTitleCase_usingUpperCaseString_returnsTitleCaseString() throws Exception {
        String convertedString = GCUtil.convertToTitleCase(mMockContext, "TITLE CASE TEST STRING");
        Assert.assertEquals("Title Case Test String", convertedString);
    }

    @Test
    public void testConvertToTitleCase_usingLowerCaseString_returnsTitleCaseString() throws Exception {
        String convertedString = GCUtil.convertToTitleCase(mMockContext, "title case test string");
        Assert.assertEquals("Title Case Test String", convertedString);
    }

    @Test
    public void testConvertToTitleCase_usingWackyCaseString_returnsTitleCaseString() throws Exception {
        String convertedString = GCUtil.convertToTitleCase(mMockContext, "tItlE CaSe tesT StRiNg");
        Assert.assertEquals("Title Case Test String", convertedString);
    }

    @Test
    public void testConvertToTitleCase_usingSpecificString_returnsSpecialCase() throws Exception {
        String inputString = "OG CHROMA";

        // Given a mocked Context injected into the object under test...
        when(mMockContext.getString(R.string.OG_CHROMA)).thenReturn(inputString);

        String convertedString = GCUtil.convertToTitleCase(mMockContext, inputString);
        Assert.assertEquals("OG Chroma", convertedString);
    }

}