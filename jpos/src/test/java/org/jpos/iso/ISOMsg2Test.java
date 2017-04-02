/*
 * jPOS Project [http://jpos.org]
 * Copyright (C) 2000-2017 jPOS Software SRL
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jpos.iso;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import org.jpos.iso.channel.LogChannel;
import org.jpos.iso.header.BASE1Header;
import org.jpos.iso.header.BaseHeader;
import org.jpos.iso.packager.EuroSubFieldPackager;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.iso.packager.GenericValidatingPackager;
import org.jpos.iso.packager.ISO87APackager;
import org.jpos.iso.packager.ISO87APackagerBBitmap;
import org.jpos.iso.packager.X92Packager;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ISOMsg2Test {

    @BeforeClass
    public static void onClassSetup() {
        assumeThat(System.getProperty("executeQuickRunningTestsOnly", "false"), is("false"));
    }

    @Test
    public void testClone() throws Throwable {
        byte[] header = new byte[2];
        ISOHeader header2 = new BASE1Header(header);
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.setHeader(header2);
        ISOMsg result = (ISOMsg) iSOMsg.clone();
        assertEquals("result.getDirection()", 0, result.getDirection());
        assertEquals("iSOMsg.fields.size()", 0, iSOMsg.fields.size());
        assertSame("iSOMsg.header", header2, iSOMsg.header);
    }

    @Test
    public void testClone1() throws Throwable {
        ISOMsg iSOVMsg = new ISOVMsg(new ISOMsg(), new ISOVError("testISOMsgDescription", "testISOMsgRejectCode"));
        ISOVMsg result = (ISOVMsg) iSOVMsg.clone();
        assertNotNull("result", result);
        assertEquals("(ISOVMsg) iSOVMsg.fields.size()", 0, ((ISOVMsg) iSOVMsg).fields.size());
    }

    @Test
    public void testClone2() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        int[] fields = new int[5];
        ISOMsg result = (ISOMsg) iSOMsg.clone(fields);
        assertEquals("result.getDirection()", 0, result.getDirection());
        assertEquals("iSOMsg.fields.size()", 0, iSOMsg.fields.size());
    }

    @Test
    public void testClone3() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        int[] fields = new int[0];
        ISOMsg result = (ISOMsg) iSOMsg.clone(fields);
        assertEquals("result.getDirection()", 0, result.getDirection());
        assertEquals("iSOMsg.fields.size()", 0, iSOMsg.fields.size());
    }

    @Test
    public void testCloneThrowsNullPointerException() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        try {
            iSOMsg.clone(null);
            fail("Expected NullPointerException to be thrown");
        } catch (NullPointerException ex) {
            assertNull("ex.getMessage()", ex.getMessage());
            assertEquals("iSOMsg.fields.size()", 0, iSOMsg.fields.size());
        }
    }

    @Test
    public void testConstructor() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        assertEquals("iSOMsg.fields.size()", 1, iSOMsg.fields.size());
        assertEquals("iSOMsg.direction", 0, iSOMsg.direction);
        assertNull("iSOMsg.header", iSOMsg.header);
        assertEquals("iSOMsg.maxField", 0, iSOMsg.maxField);
        assertTrue("iSOMsg.dirty", iSOMsg.dirty);
        assertEquals("iSOMsg.fieldNumber", -1, iSOMsg.fieldNumber);
        assertTrue("iSOMsg.maxFieldDirty", iSOMsg.maxFieldDirty);
    }

    @Test
    public void testConstructor1() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg(100);
        assertEquals("iSOMsg.fields.size()", 0, iSOMsg.fields.size());
        assertEquals("iSOMsg.direction", 0, iSOMsg.direction);
        assertNull("iSOMsg.header", iSOMsg.header);
        assertEquals("iSOMsg.maxField", -1, iSOMsg.maxField);
        assertTrue("iSOMsg.dirty", iSOMsg.dirty);
        assertEquals("iSOMsg.fieldNumber", 100, iSOMsg.fieldNumber);
        assertTrue("iSOMsg.maxFieldDirty", iSOMsg.maxFieldDirty);
    }

    @Test
    public void testConstructor2() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        assertEquals("iSOMsg.fields.size()", 0, iSOMsg.fields.size());
        assertEquals("iSOMsg.direction", 0, iSOMsg.direction);
        assertNull("iSOMsg.header", iSOMsg.header);
        assertEquals("iSOMsg.maxField", -1, iSOMsg.maxField);
        assertTrue("iSOMsg.dirty", iSOMsg.dirty);
        assertTrue("iSOMsg.maxFieldDirty", iSOMsg.maxFieldDirty);
        assertEquals("iSOMsg.fieldNumber", -1, iSOMsg.fieldNumber);
    }

    @Test
    public void testDump() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.setFieldNumber(100);
        PrintStream p = new PrintStream(new ByteArrayOutputStream(), true, "US-ASCII");
        iSOMsg.dump(p, "testISOMsgIndent");
        assertTrue("Test completed without Exception", true);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetChildren() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        Map result = iSOMsg.getChildren();
        assertEquals("result.size()", 0, result.size());
        assertEquals("iSOMsg.fields.size()", 0, iSOMsg.fields.size());
    }

    @Test
    public void testGetComponent() throws Throwable {
        ISOComponent result = new ISOMsg().getComponent(100);
        assertNull("result", result);
    }

    @Test
    public void testGetComposite() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        ISOMsg result = (ISOMsg) iSOMsg.getComposite();
        assertSame("result", iSOMsg, result);
    }

    @Test
    public void testGetDirection() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.setDirection(100);
        int result = iSOMsg.getDirection();
        assertEquals("result", 100, result);
    }

    @Test
    public void testGetHeader() throws Throwable {
        byte[] header = new byte[2];
        ISOHeader header2 = new BASE1Header(header);
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.setHeader(header2);
        byte[] result = iSOMsg.getHeader();
        assertTrue (ISOUtil.hexString(header) + "/" + ISOUtil.hexString(result), Arrays.equals(header, result));
        assertEquals("header[0]", (byte) 0, header[0]);
        assertSame("iSOMsg.header", header2, iSOMsg.header);
    }

    @Test
    public void testGetHeader1() throws Throwable {
        ISOMsg iSOVMsg = new ISOVMsg(mock(ISOVMsg.class));
        byte[] result = iSOVMsg.getHeader();
        assertNull("result", result);
        assertNull("(ISOVMsg) iSOVMsg.header", ((ISOVMsg) iSOVMsg).header);
    }

    @Test
    public void testGetISOHeader() throws Throwable {
        byte[] header = new byte[2];
        ISOHeader header2 = new BASE1Header(header);
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.setHeader(header2);
        ISOHeader result = iSOMsg.getISOHeader();
        assertSame("result", header2, result);
    }

    @Test
    public void testGetISOHeader1() throws Throwable {
        ISOHeader result = new ISOMsg().getISOHeader();
        assertNull("result", result);
    }

    @Test
    public void testGetKey() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.setFieldNumber(-2);
        Integer result = (Integer) iSOMsg.getKey();
        assertEquals("result", -2, result.intValue());
    }

    @Test
    public void testGetKey1() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.setFieldNumber(0);
        Integer result = (Integer) iSOMsg.getKey();
        assertEquals("result", 0, result.intValue());
    }

    @Test
    public void testGetKeyThrowsISOException() throws Throwable {
        try {
            new ISOMsg("testISOMsgMti").getKey();
            fail("Expected ISOException to be thrown");
        } catch (IllegalStateException ex) {
            assertEquals("ex.getMessage()", "This is not a subField", ex.getMessage());
            assertNull("ex.nested", ex.getCause());
        }
    }

    @Test
    public void testGetMaxField() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.set(100, "testISOMsgValue");
        byte[] v = new byte[2];
        iSOMsg.set(new ISOBinaryField(100, v));
        iSOMsg.set(1000, (byte[]) null);
        int result = iSOMsg.getMaxField();
        assertFalse("iSOMsg.maxFieldDirty", iSOMsg.maxFieldDirty);
        assertEquals("result", 100, result);
        assertEquals("iSOMsg.maxField", 100, iSOMsg.maxField);
    }

    @Test
    public void testGetMaxField1() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        int maxField = iSOMsg.getMaxField();
        int result = iSOMsg.getMaxField();
        assertEquals("result", maxField, result);
    }

    @Test
    public void testGetMaxField2() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        int result = iSOMsg.getMaxField();
        assertEquals("iSOMsg.maxField", 0, iSOMsg.maxField);
        assertFalse("iSOMsg.maxFieldDirty", iSOMsg.maxFieldDirty);
        assertEquals("result", 0, result);
    }

    @Test
    public void testGetMTI() throws Throwable {
        String result = new ISOMsg("testISOMsgMti").getMTI();
        assertEquals("result", "testISOMsgMti", result);
    }

    @Test
    public void testGetMTI1() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.setFieldNumber(-2);
        String result = iSOMsg.getMTI();
        assertEquals("result", "testISOMsgMti", result);
    }

    @Test
    public void testGetMTIThrowsISOException() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.move(100, 0);
        try {
            iSOMsg.getMTI();
            fail("Expected ISOException to be thrown");
        } catch (ISOException ex) {
            assertEquals("ex.getMessage()", "MTI not available", ex.getMessage());
            assertNull("ex.nested", ex.nested);
        }
    }

    @Test
    public void testGetMTIThrowsISOException1() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.setFieldNumber(0);
        try {
            iSOMsg.getMTI();
            fail("Expected ISOException to be thrown");
        } catch (ISOException ex) {
            assertEquals("ex.getMessage()", "can't getMTI on inner message", ex.getMessage());
            assertNull("ex.nested", ex.nested);
        }
    }

    @Test
    public void testGetPackager() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        ISOPackager p = new GenericValidatingPackager();
        iSOMsg.setPackager(p);
        ISOPackager result = iSOMsg.getPackager();
        assertSame("result", p, result);
    }

    @Test
    public void testGetSource() throws Throwable {
        ISOSource result = new ISOMsg().getSource();
        assertNull("result", result);
    }

    @Test
    public void testGetString() throws Throwable {
        String result = new ISOMsg().getString(100);
        assertNull("result", result);
    }

    @Test
    public void testGetValue() throws Throwable {
        Object result = new ISOMsg("testISOMsgMti").getValue(100);
        assertNull("result", result);
    }

    @Test
    public void testGetValue1() throws Throwable {
        ISOMsg iSOVMsg = new ISOVMsg(new ISOMsg(), new ISOVError("testISOMsgDescription", "testISOMsgRejectCode"));
        ISOMsg result = (ISOMsg) iSOVMsg.getValue();
        assertSame("result", iSOVMsg, result);
    }

    @Test
    public void testHasField() throws Throwable {
        int[] fields = new int[2];
        boolean result = ((ISOMsg) new ISOMsg().clone(fields)).hasField(100);
        assertFalse("result", result);
    }

    @Test
    public void testHasField1() throws Throwable {
        boolean result = new ISOMsg("testISOMsgMti").hasField(0);
        assertTrue("result", result);
    }

    @Test
    public void testHasFields() throws Throwable {
        int[] fields = new int[0];
        boolean result = new ISOMsg().hasFields(fields);
        assertTrue("result", result);
    }

    @Test
    public void testHasFields1() throws Throwable {
        int[] fields = new int[4];
        fields[1] = Integer.MIN_VALUE;
        boolean result = new ISOMsg("testISOMsgMti").hasFields(fields);
        assertFalse("result", result);
    }

    @Test
    public void testHasFields2() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg(100);
        iSOMsg.setFieldNumber(-1);
        iSOMsg.setMTI("testISOMsgMti");
        int[] fields = new int[2];
        boolean result = iSOMsg.hasFields(fields);
        assertTrue("result", result);
    }

    @Test
    public void testHasFields3() throws Throwable {
        int[] fields = new int[2];
        fields[0] = -2;
        boolean result = new ISOMsg("testISOMsgMti").hasFields(fields);
        assertFalse("result", result);
    }

    @Test
    public void testHasFieldsThrowsNullPointerException() throws Throwable {
        try {
            new ISOMsg("testISOMsgMti").hasFields(null);
            fail("Expected NullPointerException to be thrown");
        } catch (NullPointerException ex) {
            assertNull("ex.getMessage()", ex.getMessage());
        }
    }

    @Test
    public void testIsIncoming() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.setDirection(1);
        boolean result = iSOMsg.isIncoming();
        assertTrue("result", result);
    }

    @Test
    public void testIsIncoming1() throws Throwable {
        boolean result = new ISOMsg("testISOMsgMti").isIncoming();
        assertFalse("result", result);
    }

    @Test
    public void testIsIncoming2() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.setDirection(2);
        boolean result = iSOMsg.isIncoming();
        assertFalse("result", result);
    }

    @Test
    public void testIsInner() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.setFieldNumber(0);
        boolean result = iSOMsg.isInner();
        assertTrue("result", result);
    }

    @Test
    public void testIsInner1() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.setFieldNumber(-2);
        boolean result = iSOMsg.isInner();
        assertFalse("result", result);
    }

    @Test
    public void testIsInner2() throws Throwable {
        boolean result = new ISOMsg("testISOMsgMti").isInner();
        assertFalse("result", result);
    }

    @Test
    public void testIsOutgoing() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.setDirection(2);
        boolean result = iSOMsg.isOutgoing();
        assertTrue("result", result);
    }

    @Test
    public void testIsOutgoing1() throws Throwable {
        boolean result = new ISOMsg().isOutgoing();
        assertFalse("result", result);
    }

    @Test
    public void testIsRequest() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.setFieldNumber(-7);
        boolean result = iSOMsg.isRequest();
        assertTrue("result", result);
    }

    @Test
    public void testIsRequest1() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.setMTI("testISOMsgMti");
        iSOMsg.setFieldNumber(-2);
        boolean result = iSOMsg.isRequest();
        assertTrue("result", result);
    }

    @Test
    public void testIsRequest2() throws Throwable {
        boolean result = new ISOMsg("   ").isRequest();
        assertFalse("result", result);
    }

    @Test
    public void testIsRequestThrowsISOException() throws Throwable {
        try {
            new ISOMsg().isRequest();
            fail("Expected ISOException to be thrown");
        } catch (ISOException ex) {
            assertEquals("ex.getMessage()", "MTI not available", ex.getMessage());
            assertNull("ex.nested", ex.nested);
        }
    }

    @Test
    public void testIsRequestThrowsISOException1() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.setFieldNumber(0);
        try {
            iSOMsg.isRequest();
            fail("Expected ISOException to be thrown");
        } catch (ISOException ex) {
            assertEquals("ex.getMessage()", "can't getMTI on inner message", ex.getMessage());
            assertNull("ex.nested", ex.nested);
        }
    }

    @Test
    public void testIsRequestThrowsStringIndexOutOfBoundsException() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("");
        iSOMsg.setFieldNumber(-2);
        try {
            iSOMsg.isRequest();
            fail("Expected StringIndexOutOfBoundsException to be thrown");
        } catch (StringIndexOutOfBoundsException ex) {
            assertEquals("ex.getMessage()", "String index out of range: 2", ex.getMessage());
        }
    }

    @Test
    public void testIsResponse() throws Throwable {
        boolean result = new ISOMsg("XXX X XXXXXXXX").isResponse();
        assertTrue("result", result);
    }

    @Test
    public void testIsResponse1() throws Throwable {
        boolean result = new ISOMsg("testISOMsgMti").isResponse();
        assertFalse("result", result);
    }

    @Test
    public void testIsResponse2() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.setFieldNumber(-2);
        boolean result = iSOMsg.isResponse();
        assertFalse("result", result);
    }

    @Test
    public void testIsResponseThrowsISOException() throws Throwable {
        try {
            new ISOMsg().isResponse();
            fail("Expected ISOException to be thrown");
        } catch (ISOException ex) {
            assertEquals("ex.getMessage()", "MTI not available", ex.getMessage());
            assertNull("ex.nested", ex.nested);
        }
    }

    @Test
    public void testIsResponseThrowsISOException1() throws Throwable {
        try {
            new ISOMsg(0).isResponse();
            fail("Expected ISOException to be thrown");
        } catch (ISOException ex) {
            assertEquals("ex.getMessage()", "can't getMTI on inner message", ex.getMessage());
            assertNull("ex.nested", ex.nested);
        }
    }

    @Test
    public void testIsResponseThrowsStringIndexOutOfBoundsException() throws Throwable {
        try {
            new ISOMsg("").isResponse();
            fail("Expected StringIndexOutOfBoundsException to be thrown");
        } catch (StringIndexOutOfBoundsException ex) {
            assertEquals("ex.getMessage()", "String index out of range: 2", ex.getMessage());
        }
    }

    @Test
    public void testIsRetransmissionThrowsISOException() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.setFieldNumber(100);
        try {
            iSOMsg.isRetransmission();
            fail("Expected ISOException to be thrown");
        } catch (ISOException ex) {
            assertEquals("ex.getMessage()", "can't getMTI on inner message", ex.getMessage());
            assertNull("ex.nested", ex.nested);
        }
    }

    @Test
    public void testMerge() throws Throwable {
        ISOMsg m = new ISOMsg(100);
        new ISOMsg().merge(m);
        assertEquals("m.maxField", 0, m.maxField);
        assertFalse("m.maxFieldDirty", m.maxFieldDirty);
    }

    @Test
    public void testMerge1() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        ISOMsg m = new ISOMsg();
        m.recalcBitMap();
        byte[] value = new byte[0];
        m.set(3, value);
        iSOMsg.merge(m);
        assertEquals("iSOMsg.fields.size()", 2, iSOMsg.fields.size());
        assertEquals("iSOMsg.maxField", 3, iSOMsg.maxField);
        assertTrue("iSOMsg.dirty", iSOMsg.dirty);
    }

    @Test
    public void testMerge2() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        ISOMsg m = new ISOMsg("testISOMsgMti");
        iSOMsg.merge(m);
        assertEquals("iSOMsg.fields.size()", 1, iSOMsg.fields.size());
        assertEquals("iSOMsg.maxField", 0, iSOMsg.maxField);
        assertFalse("m.maxFieldDirty", m.maxFieldDirty);
        assertTrue("iSOMsg.dirty", iSOMsg.dirty);
    }

    @Test
    public void testMergeThrowsNullPointerException() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        try {
            iSOMsg.merge(null);
            fail("Expected NullPointerException to be thrown");
        } catch (NullPointerException ex) {
            assertNull("ex.getMessage()", ex.getMessage());
            assertEquals("iSOMsg.fields.size()", 0, iSOMsg.fields.size());
            assertEquals("iSOMsg.maxField", -1, iSOMsg.maxField);
            assertTrue("iSOMsg.dirty", iSOMsg.dirty);
        }
    }

    @Test
    public void testMove() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.move(100, 1000);
        assertEquals("iSOMsg.fields.size()", 0, iSOMsg.fields.size());
    }

    @Test
    public void testPack() throws Throwable {
        ISOPackager p = new ISO87APackagerBBitmap();
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.setPackager(p);
        iSOMsg.getMaxField();
        byte[] result = iSOMsg.pack();
        assertEquals("iSOMsg.fields.size()", 1, iSOMsg.fields.size());
        assertFalse("iSOMsg.dirty", iSOMsg.dirty);
        assertEquals("result.length", 0, result.length);
        assertSame("iSOMsg.packager", p, iSOMsg.packager);
    }

    @Test
    public void testPackThrowsISOException() throws Throwable {
        ISOPackager p = new EuroSubFieldPackager();
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.setPackager(p);
        iSOMsg.recalcBitMap();
        iSOMsg.move(-1, 100);
        try {
            iSOMsg.pack();
            fail("Expected ISOException to be thrown");
        } catch (ISOException ex) {
            assertEquals("iSOMsg.fields.size()", 2, iSOMsg.fields.size());
            assertFalse("iSOMsg.dirty", iSOMsg.dirty);
            assertFalse("iSOMsg.maxFieldDirty", iSOMsg.maxFieldDirty);
            assertEquals("ex.getMessage()", "java.lang.NullPointerException", ex.getMessage());
            assertNull("ex.nested.getMessage()", ex.nested.getMessage());
            assertEquals("iSOMsg.maxField", 100, iSOMsg.maxField);
            assertSame("iSOMsg.packager", p, iSOMsg.packager);
        }
    }

    @Test
    public void testPackThrowsNegativeArraySizeException() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        byte[] value = new byte[3];
        iSOMsg.set(Integer.MAX_VALUE, value);
        try {
            iSOMsg.pack();
            fail("Expected NegativeArraySizeException to be thrown");
        } catch (NullPointerException ex) {
            assertFalse("iSOMsg.maxFieldDirty", iSOMsg.maxFieldDirty);
            assertEquals("iSOMsg.maxField", Integer.MAX_VALUE, iSOMsg.maxField);
            assertNull("iSOMsg.packager", iSOMsg.packager);
            assertEquals("iSOMsg.fields.size()", 2, iSOMsg.fields.size());
            assertFalse("iSOMsg.dirty", iSOMsg.dirty);
        }
    }

    @Test
    public void testPackThrowsNullPointerException() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.setMTI("testISOMsgMti");
        try {
            iSOMsg.pack();
            fail("Expected NullPointerException to be thrown");
        } catch (NullPointerException ex) {
            assertEquals("iSOMsg.fields.size()", 2, iSOMsg.fields.size());
            assertFalse("iSOMsg.dirty", iSOMsg.dirty);
            assertFalse("iSOMsg.maxFieldDirty", iSOMsg.maxFieldDirty);
            assertNull("ex.getMessage()", ex.getMessage());
            assertEquals("iSOMsg.maxField", 0, iSOMsg.maxField);
            assertNull("iSOMsg.packager", iSOMsg.packager);
        }
    }

    @Test
    public void testPackThrowsNullPointerException1() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.recalcBitMap();
        try {
            iSOMsg.pack();
            fail("Expected NullPointerException to be thrown");
        } catch (NullPointerException ex) {
            assertNull("ex.getMessage()", ex.getMessage());
            assertNull("iSOMsg.packager", iSOMsg.packager);
            assertEquals("iSOMsg.fields.size()", 1, iSOMsg.fields.size());
            assertEquals("iSOMsg.maxField", 0, iSOMsg.maxField);
            assertFalse("iSOMsg.dirty", iSOMsg.dirty);
            assertFalse("iSOMsg.maxFieldDirty", iSOMsg.maxFieldDirty);
        }
    }

    @Test
    public void testReadHeaderThrowsNullPointerException() throws Throwable {
        ISOMsg iSOVMsg = new ISOVMsg(new ISOMsg("testISOMsgMti"), new ISOVError("testISOMsgDescription"));
        try {
            iSOVMsg.readHeader(null);
            fail("Expected NullPointerException to be thrown");
        } catch (NullPointerException ex) {
            assertNull("ex.getMessage()", ex.getMessage());
            assertNull("(ISOVMsg) iSOVMsg.header", ((ISOVMsg) iSOVMsg).header);
        }
    }

    @Test
    public void testRecalcBitMap() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.recalcBitMap();
        assertEquals("iSOMsg.fields.size()", 2, iSOMsg.fields.size());
        assertFalse("iSOMsg.dirty", iSOMsg.dirty);
        assertFalse("iSOMsg.maxFieldDirty", iSOMsg.maxFieldDirty);
        assertEquals("iSOMsg.maxField", 0, iSOMsg.maxField);
    }

    @Test
    public void testRecalcBitMap1() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.set(100, "testISOMsgValue");
        iSOMsg.set(1000, (byte[]) null);
        iSOMsg.recalcBitMap();
        assertEquals("iSOMsg.fields.size()", 2, iSOMsg.fields.size());
        assertFalse("iSOMsg.dirty", iSOMsg.dirty);
        assertFalse("iSOMsg.maxFieldDirty", iSOMsg.maxFieldDirty);
        assertEquals("iSOMsg.maxField", 100, iSOMsg.maxField);
    }

    @Test
    public void testRecalcBitMap2() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg(100);
        iSOMsg.recalcBitMap();
        iSOMsg.set(0, "testISOMsgValue");
        iSOMsg.recalcBitMap();
        assertFalse("iSOMsg.dirty", iSOMsg.dirty);
        assertEquals("iSOMsg.fields.size()", 2, iSOMsg.fields.size());
    }

    @Test
    public void testRecalcBitMap3() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.recalcBitMap();
        ISOMsg m = new ISOMsg();
        m.set(1, "testISOMsgValue");
        iSOMsg.merge(m);
        iSOMsg.recalcBitMap();
        assertFalse("iSOMsg.dirty", iSOMsg.dirty);
        assertEquals("iSOMsg.fields.size()", 3, iSOMsg.fields.size());
    }

    @Test
    public void testRecalcBitMap4() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg(100);
        iSOMsg.recalcBitMap();
        assertEquals("iSOMsg.fields.size()", 1, iSOMsg.fields.size());
        assertEquals("iSOMsg.maxField", 0, iSOMsg.maxField);
        assertFalse("iSOMsg.dirty", iSOMsg.dirty);
        assertFalse("iSOMsg.maxFieldDirty", iSOMsg.maxFieldDirty);
    }

    @Test
    public void testRecalcBitMap5() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.set(100, "testISOMsgValue");
        iSOMsg.recalcBitMap();
        byte[] value = new byte[2];
        iSOMsg.set(Integer.valueOf(100), value);
        iSOMsg.recalcBitMap();
        assertFalse("iSOMsg.dirty", iSOMsg.dirty);
        assertEquals("iSOMsg.fields.size()", 2, iSOMsg.fields.size());
    }

    @Test
    public void testRecalcBitMap6() throws Throwable {
        byte[] value = new byte[3];
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.set(1, value);
        iSOMsg.recalcBitMap();
        assertEquals("iSOMsg.fields.size()", 2, iSOMsg.fields.size());
        assertFalse("iSOMsg.dirty", iSOMsg.dirty);
        assertFalse("iSOMsg.maxFieldDirty", iSOMsg.maxFieldDirty);
        assertEquals("iSOMsg.maxField", 1, iSOMsg.maxField);
    }

    @Test
    public void testSerializeDeserializeThenCompare() throws Exception {
        ISOMsg msg = new ISOMsg();
        msg.setMTI("0800");

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(buffer);
        out.writeObject(msg);
        out.close();

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        ISOMsg dest = (ISOMsg) in.readObject();
        in.close();
        assertEquals("obj != deserialize(serialize(obj))", msg.getMTI(), dest.getMTI());
    }

    @Test
    public void testSet() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.setPackager(new ISO87APackagerBBitmap());
        iSOMsg.set(100, "testISOMsgValue");
        assertEquals("iSOMsg.fields.size()", 1, iSOMsg.fields.size());
        assertEquals("iSOMsg.maxField", 100, iSOMsg.maxField);
        assertTrue("iSOMsg.dirty", iSOMsg.dirty);
    }

    @Test
    public void testSet1() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.set(100, "testISOMsgValue");
        assertEquals("iSOMsg.fields.size()", 1, iSOMsg.fields.size());
        assertEquals("iSOMsg.maxField", 100, iSOMsg.maxField);
        assertTrue("iSOMsg.dirty", iSOMsg.dirty);
    }

    @Test
    public void testSet2() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.setPackager(new X92Packager());
        iSOMsg.set(64, "testISOMsgValue1");
        assertEquals("iSOMsg.fields.size()", 2, iSOMsg.fields.size());
        assertEquals("iSOMsg.maxField", 64, iSOMsg.maxField);
        assertTrue("iSOMsg.dirty", iSOMsg.dirty);
    }

    @Test
    public void testSet3() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.set(100, (String) null);
        assertEquals("iSOMsg.fields.size()", 0, iSOMsg.fields.size());
    }

    @Test
    public void testSet4() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.set(new ISOMsg(-2));
        assertEquals("iSOMsg.fields.size()", 1, iSOMsg.fields.size());
        assertTrue("iSOMsg.dirty", iSOMsg.dirty);
    }

    @Test
    public void testSet5() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.setFieldNumber(0);
        ISOMsg iSOMsg2 = new ISOMsg();
        iSOMsg2.set(100, "testISOMsgValue");
        int[] fields = new int[2];
        ISOMsg clone = (ISOMsg) iSOMsg2.clone(fields);
        clone.set((ISOComponent) iSOMsg.clone());
        assertEquals("clone.fields.size()", 1, clone.fields.size());
        assertTrue("clone.dirty", clone.dirty);
    }

    @Test
    public void testSet6() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.set(new ISOMsg(100));
        assertEquals("iSOMsg.fields.size()", 2, iSOMsg.fields.size());
        assertEquals("iSOMsg.maxField", 100, iSOMsg.maxField);
        assertTrue("iSOMsg.dirty", iSOMsg.dirty);
    }

    @Test
    public void testSet7() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.set(100, (byte[]) null);
        assertEquals("iSOMsg.fields.size()", 0, iSOMsg.fields.size());
    }

    @Test
    public void testSet8() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        byte[] value = new byte[2];
        iSOMsg.set(100, value);
        assertEquals("iSOMsg.fields.size()", 1, iSOMsg.fields.size());
        assertEquals("iSOMsg.maxField", 100, iSOMsg.maxField);
        assertTrue("iSOMsg.dirty", iSOMsg.dirty);
    }

    @Test
    public void testSetDirection() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.setDirection(100);
        assertEquals("iSOMsg.direction", 100, iSOMsg.direction);
    }

    @Test
    public void testSetFieldNumber() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.setFieldNumber(100);
        assertEquals("iSOMsg.fieldNumber", 100, iSOMsg.fieldNumber);
    }

    @Test
    public void testSetHeader() throws Throwable {
        byte[] header = new byte[2];
        ISOHeader header2 = new BASE1Header(header);
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.setHeader(header2);
        assertSame("iSOMsg.header", header2, iSOMsg.header);
    }

    @Test
    public void testSetHeader1() throws Throwable {
        byte[] b = new byte[2];
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.setHeader(b);
        assertNull("iSOMsg.header.getDestination()", iSOMsg.header.getDestination());
    }

    @Test
    public void testSetMTI() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.setMTI("testISOMsgMti");
        assertEquals("iSOMsg.fields.size()", 1, iSOMsg.fields.size());
        assertEquals("iSOMsg.maxField", 0, iSOMsg.maxField);
        assertTrue("iSOMsg.dirty", iSOMsg.dirty);
    }

    @Test
    public void testSetMTI1() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.setFieldNumber(-2);
        iSOMsg.setMTI("testISOMsgMti");
        assertEquals("iSOMsg.fields.size()", 1, iSOMsg.fields.size());
        assertTrue("iSOMsg.dirty", iSOMsg.dirty);
    }

    @Test
    public void testSetMTIThrowsISOException() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.setFieldNumber(0);
        try {
            iSOMsg.setMTI("testISOMsgMti");
            fail("Expected ISOException to be thrown");
        } catch (ISOException ex) {
            assertEquals("ex.getMessage()", "can't setMTI on inner message", ex.getMessage());
            assertNull("ex.nested", ex.nested);
            assertEquals("iSOMsg.fields.size()", 1, iSOMsg.fields.size());
            assertEquals("iSOMsg.maxField", 0, iSOMsg.maxField);
            assertTrue("iSOMsg.dirty", iSOMsg.dirty);
        }
    }

    @Test
    public void testSetPackager() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        ISOPackager p = new GenericValidatingPackager();
        iSOMsg.setPackager(p);
        assertSame("iSOMsg.packager", p, iSOMsg.packager);
    }

    @Test
    public void testSetResponseMTI() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.setMTI("testISOMsgMti");
        iSOMsg.setResponseMTI();
        assertEquals("iSOMsg.fields.get(Integer.valueOf(0)).value", "te290",
                ((ISOField) iSOMsg.fields.get(Integer.valueOf(0))).value);
        assertEquals("iSOMsg.fields.size()", 1, iSOMsg.fields.size());
        assertTrue("iSOMsg.dirty", iSOMsg.dirty);
    }

    @Test
    public void testSetResponseMTI1() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.setMTI(" di2rection=\"outgoing\"");
        iSOMsg.setResponseMTI();
        assertEquals("iSOMsg.fields.get(Integer.valueOf(0)).value", " d192",
                ((ISOField) iSOMsg.fields.get(Integer.valueOf(0))).value);
        assertEquals("iSOMsg.fields.size()", 1, iSOMsg.fields.size());
        assertTrue("iSOMsg.dirty", iSOMsg.dirty);
    }

    @Test
    public void testSetResponseMTIThrowsISOException() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.setFieldNumber(0);
        try {
            iSOMsg.setResponseMTI();
            fail("Expected ISOException to be thrown");
        } catch (ISOException ex) {
            assertEquals("ex.getMessage()", "can't getMTI on inner message", ex.getMessage());
            assertNull("ex.nested", ex.nested);
            assertEquals("iSOMsg.fields.size()", 0, iSOMsg.fields.size());
            assertEquals("iSOMsg.maxField", -1, iSOMsg.maxField);
            assertTrue("iSOMsg.dirty", iSOMsg.dirty);
        }
    }

    @Test
    public void testSetResponseMTIThrowsStringIndexOutOfBoundsException() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("");
        try {
            iSOMsg.setResponseMTI();
            fail("Expected StringIndexOutOfBoundsException to be thrown");
        } catch (StringIndexOutOfBoundsException ex) {
            assertEquals("ex.getMessage()", "String index out of range: 2", ex.getMessage());
            assertEquals("iSOMsg.fields.size()", 1, iSOMsg.fields.size());
            assertEquals("iSOMsg.maxField", 0, iSOMsg.maxField);
            assertTrue("iSOMsg.dirty", iSOMsg.dirty);
        }
    }

    @Test
    public void testSetRetransmissionMTI() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.setMTI("testISOMsgMti");
        iSOMsg.setRetransmissionMTI();
        assertEquals("iSOMsg.fields.get(Integer.valueOf(0)).value", "tes1", ((ISOField) iSOMsg.fields.get(Integer.valueOf(0))).value);
        assertEquals("iSOMsg.fields.size()", 1, iSOMsg.fields.size());
        assertTrue("iSOMsg.dirty", iSOMsg.dirty);
    }

    @Test
    public void testSetRetransmissionMTI1() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.setFieldNumber(-2);
        iSOMsg.setRetransmissionMTI();
        assertEquals("iSOMsg.fields.get(Integer.valueOf(0)).value", "tes1", ((ISOField) iSOMsg.fields.get(Integer.valueOf(0))).value);
        assertEquals("iSOMsg.fields.size()", 1, iSOMsg.fields.size());
        assertTrue("iSOMsg.dirty", iSOMsg.dirty);
    }

    @Test
    public void testSetRetransmissionMTIThrowsStringIndexOutOfBoundsException() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.setMTI("");
        try {
            iSOMsg.setRetransmissionMTI();
            fail("Expected StringIndexOutOfBoundsException to be thrown");
        } catch (StringIndexOutOfBoundsException ex) {
            assertEquals("ex.getMessage()", "String index out of range: 2", ex.getMessage());
            assertEquals("iSOMsg.fields.size()", 1, iSOMsg.fields.size());
            assertEquals("iSOMsg.maxField", 0, iSOMsg.maxField);
            assertTrue("iSOMsg.dirty", iSOMsg.dirty);
        }
    }

    @Test
    public void testSetSource() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        ISOSource source = new LogChannel("testISOMsgHost", 100, new ISO87APackager());
        iSOMsg.setSource(source);
        assertSame("iSOMsg.getSource()", source, iSOMsg.getSource());
    }

    @Test
    public void testSetThrowsISOException() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        try {
            iSOMsg.set(new ISOMsg());
            fail("Expected ISOException to be thrown");
        } catch (IllegalStateException ex) {
            assertEquals("ex.getMessage()", "This is not a subField", ex.getMessage());
            assertNull("ex.nested", ex.getCause());
            assertEquals("iSOMsg.fields.size()", 0, iSOMsg.fields.size());
            assertEquals("iSOMsg.maxField", -1, iSOMsg.maxField);
            assertTrue("iSOMsg.dirty", iSOMsg.dirty);
        }
    }

    @Test
    public void testSetThrowsNullPointerException() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.setPackager(new GenericPackager());
        try {
            iSOMsg.set(100, "testISOMsgValue");
            // fixed in 1.6.8 fail("Expected NullPointerException to be thrown");
        } catch (NullPointerException ex) {
            assertNull("ex.getMessage()", ex.getMessage());
            assertEquals("iSOMsg.fields.size()", 0, iSOMsg.fields.size());
            assertEquals("iSOMsg.maxField", -1, iSOMsg.maxField);
            assertTrue("iSOMsg.dirty", iSOMsg.dirty);
            assertTrue("iSOMsg.maxFieldDirty", iSOMsg.maxFieldDirty);
        }
    }

    @Test
    public void testSetThrowsNullPointerException1() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        try {
            iSOMsg.set(null);
            // fixed in 1.6.8fail("Expected NullPointerException to be thrown");
        } catch (NullPointerException ex) {
            assertNull("ex.getMessage()", ex.getMessage());
            assertEquals("iSOMsg.fields.size()", 0, iSOMsg.fields.size());
            assertEquals("iSOMsg.maxField", -1, iSOMsg.maxField);
            assertTrue("iSOMsg.dirty", iSOMsg.dirty);
        }
    }

    @Test
    public void testSetValueThrowsISOException() throws Throwable {
        try {
            new ISOMsg().setValue(new ISOField(100, "testISOMsgv"));
            fail("Expected ISOException to be thrown");
        } catch (ISOException ex) {
            assertEquals("ex.getMessage()", "setValue N/A in ISOMsg", ex.getMessage());
            assertNull("ex.nested", ex.nested);
        }
    }

    @Test
    public void testSimpleSerialization() throws Exception {
        ISOMsg msg = new ISOMsg();
        msg.setMTI("0800");

        byte[] objekt = writeExternalFormToBytes(msg);
        Object p = readExternalFormFromBytes(objekt);
        assertEquals(ISOMsg.class, p.getClass());
        assertEquals("0800", ((ISOMsg) p).getMTI());
    }

    @Test
    public void testToString() throws Throwable {
        String result = new ISOMsg(100).toString();
        assertEquals("result", "    null", result);
    }

    @Test
    public void testToString1() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.setDirection(100);
        String result = iSOMsg.toString();
        assertEquals("result", "    testISOMsgMti", result);
    }

    @Test
    public void testToString2() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.setDirection(1);
        String result = iSOMsg.toString();
        assertEquals("result", "<-- testISOMsgMti", result);
    }

    @Test
    public void testUnpackThrowsNullPointerException() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        byte[] b = new byte[1];
        try {
            iSOMsg.unpack(b);
            fail("Expected NullPointerException to be thrown");
        } catch (NullPointerException ex) {
            assertNull("ex.getMessage()", ex.getMessage());
            assertNull("iSOMsg.packager", iSOMsg.packager);
        }
    }

    @Test
    public void testUnset() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.unset(100);
        assertEquals("iSOMsg.fields.size()", 0, iSOMsg.fields.size());
    }

    @Test
    public void testUnset1() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        iSOMsg.setMTI("testISOMsgMti");
        iSOMsg.move(0, -2147483647);
        iSOMsg.unset(-2147483647);
        assertEquals("iSOMsg.fields.size()", 0, iSOMsg.fields.size());
        assertFalse("iSOMsg.fields.containsKey(Integer.valueOf(-2147483647))",
                iSOMsg.fields.containsKey(Integer.valueOf(-2147483647)));
        assertTrue("iSOMsg.dirty", iSOMsg.dirty);
        assertTrue("iSOMsg.maxFieldDirty", iSOMsg.maxFieldDirty);
    }

    @Test
    public void testUnset2() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        int[] flds = new int[0];
        iSOMsg.unset(flds);
        assertEquals("iSOMsg.fields.size()", 0, iSOMsg.fields.size());
        assertTrue("iSOMsg.dirty", iSOMsg.dirty);
        assertTrue("iSOMsg.maxFieldDirty", iSOMsg.maxFieldDirty);
    }

    @Test
    public void testUnset3() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg();
        int[] flds = new int[1];
        iSOMsg.unset(flds);
        assertEquals("iSOMsg.fields.size()", 0, iSOMsg.fields.size());
    }

    @Test
    public void testWriteDirection() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        ObjectOutput out = new ObjectOutputStream(new ByteArrayOutputStream());
        iSOMsg.writeDirection(out);
        // int actual = out.
    }

    @Test
    public void testWriteExternal() throws Throwable {
        ObjectOutput out = new ObjectOutputStream(new ByteArrayOutputStream());
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        iSOMsg.setPackager(new GenericPackager());
        iSOMsg.writeExternal(out);
        assertNull("iSOMsg.header", iSOMsg.header);
        // int actual = ;
    }

    @Test
    public void testWriteExternalThrowsNullPointerException() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        try {
            iSOMsg.writeExternal(null);
            fail("Expected NullPointerException to be thrown");
        } catch (NullPointerException ex) {
            assertNull("ex.getMessage()", ex.getMessage());
            assertNull("iSOMsg.header", iSOMsg.header);
        }
    }

    @Test
    public void testWriteHeader() throws Throwable {
        final ISOMsg m = mock(ISOMsg.class);
        final ISOVError isov = mock(ISOVError.class);
        ISOMsg iSOVMsg = new ISOVMsg(m, isov);
        final BASE1Header header = mock(BASE1Header.class);
        iSOVMsg.setHeader(header);
        final ObjectOutputStream out = mock(ObjectOutputStream.class);
        final byte[] bytes = new byte[1];
        bytes[0] = (byte) 0;

        given(header.getLength()).willReturn(1);
        given(header.pack()).willReturn(bytes);

        iSOVMsg.writeHeader(out);
        assertSame("(ISOVMsg) iSOVMsg.header", header, ((ISOVMsg) iSOVMsg).header);
        verify(out).write(bytes);
        verify(out).writeByte(72);
        verify(out).writeShort(1);
    }

    @Test
    public void testWriteHeader1() throws Throwable {
        ISOMsg iSOMsg = new ISOMsg("testISOMsgMti");
        final BaseHeader header = mock(BaseHeader.class);
        iSOMsg.setHeader(header);
        ObjectOutputStream out = mock(ObjectOutputStream.class);
        given(header.getLength()).willReturn(0);
        iSOMsg.writeHeader(out);
        assertSame("iSOMsg.header", header, iSOMsg.header);
    }

    @Test
    public void testWriteHeaderThrowsNullPointerException() throws Throwable {
        ISOMsg iSOVMsg = new ISOVMsg(new ISOMsg(), new ISOVError("testISOMsgDescription", "testISOMsgRejectCode"));
        ObjectOutput out = new ObjectOutputStream(new ByteArrayOutputStream());
        try {
            iSOVMsg.writeHeader(out);
            fail("Expected NullPointerException to be thrown");
        } catch (NullPointerException ex) {
            assertNull("ex.getMessage()", ex.getMessage());
            assertNull("(ISOVMsg) iSOVMsg.header", ((ISOVMsg) iSOVMsg).header);
        }
    }

    @Test
    public void testWritePackager() throws Throwable {
        // Tested by SerializabilityTestCase
    }

    @Test
    public void testWritePackagerThrowsNullPointerException() throws Throwable {
        ISOMsg iSOVMsg = new ISOVMsg(new ISOMsg(), new ISOVError("testISOMsgDescription", "testISOMsgRejectCode"));
        try {
            iSOVMsg.writePackager(null);
            fail("Expected NullPointerException to be thrown");
        } catch (NullPointerException ex) {
            assertNull("ex.getMessage()", ex.getMessage());
        }
    }

    protected Object readExternalFormFromBytes(byte[] b) throws IOException, ClassNotFoundException {
        ByteArrayInputStream stream = new ByteArrayInputStream(b);
        return readExternalFormFromStream(stream);
    }

    protected byte[] writeExternalFormToBytes(Serializable o) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        writeExternalFormToStream(o, byteStream);
        return byteStream.toByteArray();
    }

    // -----------------------------------------------------------------------
    private Object readExternalFormFromStream(InputStream stream) throws IOException, ClassNotFoundException {
        ObjectInputStream oStream = new ObjectInputStream(stream);
        return oStream.readObject();
    }

    private void writeExternalFormToStream(Serializable o, OutputStream stream) throws IOException {
        ObjectOutputStream oStream = new ObjectOutputStream(stream);
        oStream.writeObject(o);
    }

}
