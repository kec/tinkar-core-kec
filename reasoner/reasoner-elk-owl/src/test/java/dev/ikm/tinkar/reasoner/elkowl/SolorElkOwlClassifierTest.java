/*
 * Copyright © 2015 Integrated Knowledge Management (support@ikm.dev)
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
package dev.ikm.tinkar.reasoner.elkowl;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolorElkOwlClassifierTest extends SolorElkOwlDataBuilderTest {

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(SolorElkOwlClassifierTest.class);

	@Test
	public void supercs() throws Exception {
		runElkOwlReasoner();
		compare("supercs");
	}

	@Test
	public void supercsService() throws Exception {
		runElkOwlReasonerService();
		compare("supercs");
	}

}
