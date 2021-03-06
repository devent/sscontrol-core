/**
 * Copyright © 2020 Erwin Müller (erwin.mueller@anrisoftware.com)
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
package com.anrisoftware.sscontrol.inline.internal;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import com.anrisoftware.sscontrol.types.external.AppException;
import com.anrisoftware.sscontrol.types.external.HostServiceScript;
import com.anrisoftware.sscontrol.types.external.PreHost;
import com.anrisoftware.sscontrol.types.external.PreHostService;

/**
 * <i>Inline</i> service pre-script.
 *
 * @author Erwin Müller, erwin.mueller@deventm.de
 * @since 1.0
 */
public class InlinePreScriptImpl implements PreHost {

    /**
     * 
     *
     * @author Erwin Müller {@literal <erwin.mueller@deventm.de>}
     * @version 1.0
     */
    public interface InlinePreScriptImplFactory extends PreHostService {
    }

    @Override
    public void configureCompiler(Object compiler) throws AppException {
        CompilerConfiguration cc = (CompilerConfiguration) compiler;
        ImportCustomizer imports = new ImportCustomizer();
        cc.addCompilationCustomizers(imports);
    }

    @Override
    public void configureServiceScript(HostServiceScript script)
            throws AppException {
    }
}
