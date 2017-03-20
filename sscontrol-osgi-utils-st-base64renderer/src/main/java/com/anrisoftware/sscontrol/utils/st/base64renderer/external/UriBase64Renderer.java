/*
 * Copyright 2016-2017 Erwin Müller <erwin.mueller@deventm.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.anrisoftware.sscontrol.utils.st.base64renderer.external;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.anrisoftware.resources.st.external.AttributeRenderer;

/**
 * Reads a resource and base64-encodes it if the format string equals to
 * "base64".
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
@SuppressWarnings("serial")
public class UriBase64Renderer implements AttributeRenderer {

    @Override
    public String toString(Object o, String formatString, Locale locale) {
        if (formatString == null) {
            return o.toString();
        }
        switch (formatString) {
        case "base64":
            try {
                return toBase64((URI) o);
            } catch (IOException e) {
                throw new ReadResourceErrorException(e, o, formatString);
            }
        }
        return o.toString();
    }

    private String toBase64(URI uri) throws IOException {
        String str = IOUtils.toString(uri);
        byte[] encodedBytes = Base64.encodeBase64(str.getBytes());
        String encoded = new String(encodedBytes);
        return encoded.replaceAll("\n", "");
    }

    @Override
    public Class<?> getAttributeType() {
        return URI.class;
    }

}
