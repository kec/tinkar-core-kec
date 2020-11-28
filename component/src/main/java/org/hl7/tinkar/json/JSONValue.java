/*
 * Copyright 2020 kec.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hl7.tinkar.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.hl7.tinkar.json.parser.JSONParser;
import org.hl7.tinkar.json.parser.ParseException;

/**
 * Original obtained from: https://github.com/fangyidong/json-simple under
 * Apache 2 license Original project had no support for Java Platform Module
 * System, and not updated for 8 years. Integrated here to integrate with Java
 * Platform Module System.
 *
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
public class JSONValue {

    /**
     * Parse JSON text into java object from the input source.
     *
     * @see org.hl7.tinkar.parser.JSONParser
     *
     * @param in
     * @return Instance of the following: org.hl7.tinkar.JSONObject,
     * org.hl7.tinkar.JSONArray, java.lang.String, java.lang.Number,
     * java.lang.Boolean, null
     *
     * @throws IOException
     * @throws ParseException
     */
    public static Object parse(Reader in) throws ParseException {
        JSONParser parser = new JSONParser();
        return parser.parse(in);
    }

    public static Object parse(String s) throws ParseException {
        JSONParser parser = new JSONParser();
        return parser.parse(s);
    }

    /**
     * Encode an object into JSON text and write it to out.
     * <p>
     * If this object is a Map or a List, and it's also a JSONStreamAware or a
     * JSONAware, JSONStreamAware or JSONAware will be considered firstly.
     * <p>
     * DO NOT call this method from writeJSONString(Writer) of a class that
     * implements both JSONStreamAware and (Map or List) with "this" as the
     * first parameter, use JSONObject.writeJSONString(Map, Writer) or
     * JSONArray.writeJSONString(List, Writer) instead.
     *
     * @param value
     * @param out
     * @throws java.io.IOException
     * @see org.hl7.tinkar.JSONObject#writeJSONString(Map, Writer)
     * @see org.hl7.tinkar.JSONArray#writeJSONString(List, Writer)
     *
     */
    public static void writeJSONString(Object value, Writer out) throws IOException {
        if (value == null) {
            out.write("null");
        } else if (value instanceof String string) {
            writeQuotedEscapedString(out, string);
        } else if (value instanceof Double double1) {
            if (double1.isInfinite() || double1.isNaN()) {
                out.write("null");
            } else {
                out.write(value.toString());
            }
        } else if (value instanceof Float float1) {
            if (float1.isInfinite() || float1.isNaN()) {
                out.write("null");
            } else {
                out.write(value.toString());
            }
        } else if (value instanceof Number) {
            out.write(value.toString());
        } else if (value instanceof Boolean) {
            out.write(value.toString());
        } else if (value instanceof UUID) {
            out.write('\"');
            out.write(value.toString());
            out.write('\"');
        } else if (value instanceof Instant) {
            out.write('\"');
            out.write(value.toString());
            out.write('\"');
        } else if ((value instanceof JSONStreamAware)) {
            ((JSONStreamAware) value).writeJSONString(out);
        } else if ((value instanceof JSONAware)) {
            out.write(((JSONAware) value).toJSONString());
        } else if (value instanceof Map map) {
            JSONObject.writeJSONString(map, out);
        } else if (value instanceof Collection collection) {
            JSONArray.writeJSONString(collection, out);
        } else if (value instanceof byte[] bs) {
            JSONArray.writeJSONString(bs, out);
        } else if (value instanceof short[] ses) {
            JSONArray.writeJSONString(ses, out);
        } else if (value instanceof int[] is) {
            JSONArray.writeJSONString(is, out);
        } else if (value instanceof long[] ls) {
            JSONArray.writeJSONString(ls, out);
        } else if (value instanceof float[] fs) {
            JSONArray.writeJSONString(fs, out);
        } else if (value instanceof double[] ds) {
            JSONArray.writeJSONString(ds, out);
        } else if (value instanceof boolean[] bs) {
            JSONArray.writeJSONString(bs, out);
        } else if (value instanceof char[] cs) {
            JSONArray.writeJSONString(cs, out);
        } else if (value instanceof Object[] objects) {
            JSONArray.writeJSONString(objects, out);
        } else if (value instanceof JsonMarshalable marshalable) {
            out.write(marshalable.toJsonString());
        } else {
            out.write(value.toString());
        }

    }

    public static void writeQuotedEscapedString(Writer out, String string) throws IOException {
        out.write('\"');
        out.write(escape(string));
        out.write('\"');
    }

    /**
     * Convert an object to JSON text.
     * <p>
     * If this object is a Map or a List, and it's also a JSONAware, JSONAware
     * will be considered firstly.
     * <p>
     * DO NOT call this method from toJSONString() of a class that implements
     * both JSONAware and Map or List with "this" as the parameter, use
     * JSONObject.toJSONString(Map) or JSONArray.toJSONString(List) instead.
     *
     * @see org.hl7.tinkar.JSONObject#toJSONString(Map)
     * @see org.hl7.tinkar.JSONArray#toJSONString(List)
     *
     * @param value
     * @return JSON text, or "null" if value is null or it's an NaN or an INF
     * number.
     */
    public static String toJSONString(Object value) {
        final StringWriter writer = new StringWriter();

        try {
            writeJSONString(value, writer);
            return writer.toString();
        } catch (IOException e) {
            // This should never happen for a StringWriter
            throw new RuntimeException(e);
        }
    }

    /**
     * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters
     * (U+0000 through U+001F).
     *
     * @param s
     * @return
     */
    public static String escape(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        escape(s, sb);
        return sb.toString();
    }

    /**
     * @param s - Must not be null.
     * @param sb
     */
    static void escape(String s, StringBuilder sb) {
        final int len = s.length();
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"' ->
                    sb.append("\\\"");
                case '\\' ->
                    sb.append("\\\\");
                case '\b' ->
                    sb.append("\\b");
                case '\f' ->
                    sb.append("\\f");
                case '\n' ->
                    sb.append("\\n");
                case '\r' ->
                    sb.append("\\r");
                case '\t' ->
                    sb.append("\\t");
                case '/' ->
                    sb.append("\\/");
                default -> {
                    if (isSpecial(ch)) {
                        String ss = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(ss.toUpperCase());
                    } else {
                        sb.append(ch);
                    }
                }
            }
        }
    }

    private static boolean isSpecial(Character ch) {
        if (Character.isISOControl(ch)) {
            return true;
        }
        // UnicodeBlock GENERAL_PUNCTUATION
        // UnicodeBlock SUPERSCRIPTS_AND_SUBSCRIPTS
        // UnicodeBlock CURRENCY_SYMBOLS
        // UnicodeBlock COMBINING_MARKS_FOR_SYMBOLS
        return (ch >= '\u2000' && ch <= '\u20FF');
    }
}
