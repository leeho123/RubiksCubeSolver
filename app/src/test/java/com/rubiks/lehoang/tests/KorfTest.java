package com.rubiks.lehoang.tests;

import android.os.Environment;

import com.rubiks.lehoang.rubikssolver.Korfs.Korfs;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowEnvironment;

import java.io.File;
import java.io.IOException;

/**
 * Created by LeHoang on 08/04/2015.
 */
//@RunWith(RobolectricTestRunner.class)
public class KorfTest extends TestCase {

    @Test
    public void testKorfsCorner() throws Exception {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        String path = Korfs.generateCornersAndroid();
        File file = new File(path);

        Assert.assertTrue(file.exists());
    }

    public void testKorfsCornerOnPC() throws Exception{
        File file = new File(Korfs.CORNERS_FILE_NAME);
        Korfs.generateCornerHeuristics(file);
    }

    public void testKorfsFirstEdgeOnPC() throws Exception{
        File file = new File(Korfs.FIRST_EDGE_FILE_NAME);
        Korfs.generateFirstEdgeHeuristics(file);
    }

    public void testKorfsSecondEdgeOnPC() throws Exception{
        File file = new File(Korfs.SECOND_EDGE_FILE_NAME);
        Korfs.generateSecondEdgeHeuristics(file);
    }

    public void testLoadCornerOnPC() throws IOException{
       File file = new File(Korfs.CORNERS_FILE_NAME);
       Korfs.loadCorners(file);

       Assert.assertEquals(40320, Korfs.cornerMap.size());
    }

    public void testLoadFirstEdgesOnPC() throws IOException{
        File file = new File(Korfs.FIRST_EDGE_FILE_NAME);
        Korfs.loadFirstEdges(file);

        Assert.assertEquals(665280, Korfs.firstEdgeMap.size());
    }

    public void testLoadSecondEdgesOnPC() throws IOException {
        File file = new File(Korfs.SECOND_EDGE_FILE_NAME);
        Korfs.loadSecondEdges(file);
        Assert.assertEquals(665280, Korfs.secondEdgeMap.size());
    }

}
