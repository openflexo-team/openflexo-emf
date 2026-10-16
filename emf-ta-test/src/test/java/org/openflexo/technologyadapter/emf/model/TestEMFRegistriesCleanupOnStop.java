/**
 * 
 * Copyright (c) 2015, Openflexo
 * 
 * This file is part of Emfconnector, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.technologyadapter.emf.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.foundation.test.OpenflexoTestCase;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.EMFTechnologyContextManager;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Check that a {@link EMFTechnologyContextManager} releases, when the service manager is stopped, what it registered in the JVM-global EMF
 * registries: those are static, thus shared by every service manager of the JVM, while the EPackage and Resource.Factory registered there
 * for a jar-based metamodel are instances coming from a class loader we built for that session only.
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestEMFRegistriesCleanupOnStop extends OpenflexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestEMFRegistriesCleanupOnStop.class.getPackage().getName());

	private static final String CITY1_MM_URI = "http://www.thalesgroup.com/openflexo/emf/model/city1";

	private static ClassLoader city1ClassLoader;

	@Test
	@TestOrder(1)
	public void testLoadCity1MetaModel() throws Exception {

		log("testLoadCity1MetaModel()");

		instanciateTestServiceManager(EMFTechnologyAdapter.class);

		EMFMetaModelResource city1MMRes = (EMFMetaModelResource) serviceManager.getResourceManager().getResource(CITY1_MM_URI,
				EMFMetaModel.class);
		assertNotNull(city1MMRes);
		assertNotNull(city1MMRes.getResourceData());

		EPackage city1Package = city1MMRes.getPackage();
		assertNotNull(city1Package);
		assertSame(city1Package, EPackage.Registry.INSTANCE.getEPackage(CITY1_MM_URI));

		// That metamodel comes from a jar of the resource center, hence from a class loader of our own
		city1ClassLoader = city1Package.getClass().getClassLoader();
		assertNotNull(city1ClassLoader);
		assertNotSame(EMFTechnologyContextManager.class.getClassLoader(), city1ClassLoader);
	}

	@Test
	@TestOrder(2)
	public void testGlobalRegistriesAreReleasedOnStop() {

		log("testGlobalRegistriesAreReleasedOnStop()");

		unloadServiceManager();

		assertNull(EPackage.Registry.INSTANCE.get(CITY1_MM_URI));
		assertFalse(containsValueLoadedByCity1ClassLoader(EPackage.Registry.INSTANCE));
		assertFalse(containsValueLoadedByCity1ClassLoader(Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()));
	}

	@Test
	@TestOrder(3)
	public void testNextServiceManagerBuildsItsOwnClassLoader() throws Exception {

		log("testNextServiceManagerBuildsItsOwnClassLoader()");

		instanciateTestServiceManager(EMFTechnologyAdapter.class);

		EMFMetaModelResource city1MMRes = (EMFMetaModelResource) serviceManager.getResourceManager().getResource(CITY1_MM_URI,
				EMFMetaModel.class);
		assertNotNull(city1MMRes);
		assertNotNull(city1MMRes.getResourceData());

		EPackage city1Package = city1MMRes.getPackage();
		assertNotNull(city1Package);
		assertSame(city1Package, EPackage.Registry.INSTANCE.getEPackage(CITY1_MM_URI));

		// Nothing was left behind by the previous session, so this one really loaded the metamodel again
		assertNotSame(city1ClassLoader, city1Package.getClass().getClassLoader());
	}

	private static boolean containsValueLoadedByCity1ClassLoader(Map<String, Object> registry) {
		for (Object value : registry.values()) {
			if (value != null && value.getClass().getClassLoader() == city1ClassLoader) {
				return true;
			}
		}
		return false;
	}

}
