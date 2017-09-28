package com.achanr.glovercolorapp.test;

import com.achanr.glovercolorapp.models.GCChip;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Andrew Chanrasmi on 9/27/17
 */
public class GCChipTest {

    private final static String CHIP_TITLE1 = "Title1";
    private final static String CHIP_TITLE2 = "Title2";
    private final static String CHIP_COLOR1 = "Color1";
    private final static String CHIP_COLOR2 = "Color2";
    private final static String CHIP_COLOR3 = "Color3";
    private final static String CHIP_MODE1 = "Mode1";
    private final static String CHIP_MODE2 = "Mode2";
    private final static String CHIP_MODE3 = "Mode3";
    private final static ArrayList<String> TEST_COLORS_ARRAY_LIST1 = new ArrayList<>(Arrays.asList(CHIP_COLOR1, CHIP_COLOR2));
    private final static ArrayList<String> TEST_MODES_ARRAY_LIST1 = new ArrayList<>(Arrays.asList(CHIP_MODE1, CHIP_MODE2));
    private final static ArrayList<String> TEST_COLORS_ARRAY_LIST2 = new ArrayList<>(Arrays.asList(CHIP_COLOR1, CHIP_COLOR3));
    private final static ArrayList<String> TEST_MODES_ARRAY_LIST2 = new ArrayList<>(Arrays.asList(CHIP_MODE1, CHIP_MODE3));

    private GCChip mChip;

    @Before
    public void setUp() throws Exception {
        mChip = new GCChip(
                CHIP_TITLE1,
                TEST_COLORS_ARRAY_LIST1,
                TEST_MODES_ARRAY_LIST1
        );
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetTitle() throws Exception {
        Assert.assertEquals(CHIP_TITLE1, mChip.getTitle());
    }

    @Test
    public void testSetTitle() throws Exception {
        Assert.assertEquals(CHIP_TITLE1, mChip.getTitle());
        mChip.setTitle(CHIP_TITLE2);
        Assert.assertNotEquals(CHIP_TITLE1, mChip.getTitle());
        Assert.assertEquals(CHIP_TITLE2, mChip.getTitle());
    }

    @Test
    public void testGetColors() throws Exception {
        Assert.assertArrayEquals(TEST_COLORS_ARRAY_LIST1.toArray(), mChip.getColors().toArray());
    }

    @Test
    public void testSetColors() throws Exception {
        Assert.assertArrayEquals(TEST_COLORS_ARRAY_LIST1.toArray(), mChip.getColors().toArray());
        mChip.setColors(TEST_COLORS_ARRAY_LIST2);
        Assert.assertArrayEquals(TEST_COLORS_ARRAY_LIST2.toArray(), mChip.getColors().toArray());
    }

    @Test
    public void testGetModes() throws Exception {
        Assert.assertArrayEquals(TEST_MODES_ARRAY_LIST1.toArray(), mChip.getModes().toArray());
    }

    @Test
    public void testSetModes() throws Exception {
        Assert.assertArrayEquals(TEST_MODES_ARRAY_LIST1.toArray(), mChip.getModes().toArray());
        mChip.setModes(TEST_MODES_ARRAY_LIST2);
        Assert.assertArrayEquals(TEST_MODES_ARRAY_LIST2.toArray(), mChip.getModes().toArray());
    }

}