/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
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

package org.openflexo.technologyadapter.emf.rm;

import java.io.File;

import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.toolbox.FileSystemMetaDataManager;
import org.openflexo.xml.XMLRootElementInfo;

/**
 * Represents an {@link EMFMetaModelResource} stored in a simple .ecore file
 * 
 * @author sylvainguerin
 *
 */
@ModelEntity
@ImplementationClass(ECoreMetaModelResourceImpl.class)
public interface ECoreMetaModelResource extends EMFMetaModelResource {

	public static class ECoreMetaData {

		public static final String NAME_KEY = "name";
		public static final String URI_KEY = "uri";
		public static final String VERSION_KEY = "version";
		public static final String PREFIX_KEY = "prefix";

		public String name;
		public String uri;
		public String xmiVersion;
		public String prefix;

		public ECoreMetaData(FileSystemMetaDataManager metaDataManager, File file) {
			super();
			name = metaDataManager.getProperty(NAME_KEY, file);
			uri = metaDataManager.getProperty(URI_KEY, file);
			xmiVersion = metaDataManager.getProperty(VERSION_KEY, file);
			prefix = metaDataManager.getProperty(PREFIX_KEY, file);
		}

		public ECoreMetaData(XMLRootElementInfo xmlRootElementInfo) {
			super();
			/*System.out.println("OK, on cherche les metadata");
			for (String key : xmlRootElementInfo.getAttributes().keySet()) {
				System.out.println(" > " + key + " = " + xmlRootElementInfo.getAttribute(key));
			}*/
			name = xmlRootElementInfo.getAttribute("name");
			uri = xmlRootElementInfo.getAttribute("nsURI");
			xmiVersion = xmlRootElementInfo.getAttribute("version");
			prefix = xmlRootElementInfo.getAttribute("nsPrefix");
		}

		public void save(FileSystemMetaDataManager metaDataManager, File file) {
			metaDataManager.setProperty(NAME_KEY, name, file, false);
			metaDataManager.setProperty(URI_KEY, uri, file, false);
			metaDataManager.setProperty(VERSION_KEY, xmiVersion, file, false);
			metaDataManager.setProperty(PREFIX_KEY, prefix, file, false);
			System.out.println("********* Saving metadata for " + this);
			metaDataManager.saveMetaDataProperties(file);
		}

	}

	public <I> ECoreMetaData getMetaData(FlexoResourceCenter<I> resourceCenter);

}
