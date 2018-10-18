package com.anrisoftware.sscontrol.k8sbase.base.service.external;

/*-
 * #%L
 * sscontrol-osgi - k8s-base-service
 * %%
 * Copyright (C) 2016 - 2018 Advanced Natural Research Institute
 * %%
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
 * #L%
 */

import java.util.Map;

/**
 * Factory to create a node taint.
 *
 * @author Erwin Müller <erwin.mueller@deventm.de>
 * @version 1.0
 */
public interface TaintFactory {

    Taint create(Map<String, Object> args);

}
