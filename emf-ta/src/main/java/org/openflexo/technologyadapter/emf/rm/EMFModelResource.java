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

import org.eclipse.emf.ecore.resource.Resource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.EMFTechnologyContextManager;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.toolbox.FileSystemMetaDataManager;
import org.openflexo.xml.XMLRootElementInfo;

/**
 * EMF Model Resource.
 * 
 * @author gbesancon
 */
@ModelEntity
@ImplementationClass(EMFModelResourceImpl.class)
public interface EMFModelResource extends FlexoModelResource<EMFModel, EMFMetaModel, EMFTechnologyAdapter, EMFTechnologyAdapter>,
		TechnologyAdapterResource<EMFModel, EMFTechnologyAdapter> {

	public static final String TECHNOLOGY_CONTEXT_MANAGER = "technologyContextManager";

	public static final String XMI_EXTENSION = ".xmi";

	@Override
	@Getter(value = TECHNOLOGY_CONTEXT_MANAGER, ignoreType = true)
	public EMFTechnologyContextManager getTechnologyContextManager();

	@Setter(TECHNOLOGY_CONTEXT_MANAGER)
	public void setTechnologyContextManager(EMFTechnologyContextManager technologyContextManager);

	public Resource getEMFResource();

	public void setMetaModelResourceURI(String mmURI);

	public String getMetaModelResourceURI();

	public static class XMIMetaData {

		public static final String ROOT_NAMESPACE_KEY = "rootNamespace";

		public String rootNamespace;

		private XMLRootElementInfo debugXMLInfo;

		public XMIMetaData(FileSystemMetaDataManager metaDataManager, File file) {
			super();
			rootNamespace = metaDataManager.getProperty(ROOT_NAMESPACE_KEY, file);
		}

		public XMIMetaData(XMLRootElementInfo xmlRootElementInfo) {
			super();
			this.debugXMLInfo = xmlRootElementInfo;
			rootNamespace = xmlRootElementInfo.getRootNamespaceURI();
		}

		public void debug() {
			System.out.println("metadata : ");
			System.out.println("---------> " + debugXMLInfo.getRootNamespacePrefix() + " " + debugXMLInfo.getRootNamespaceURI());
			for (String ns : debugXMLInfo.getNamespaces().keySet()) {
				System.out.println(" ns > " + ns + " = " + debugXMLInfo.getNamespaceByPrefix(ns));
			}
			for (String key : debugXMLInfo.getAttributes().keySet()) {
				System.out.println(" > " + key + " = " + debugXMLInfo.getAttribute(key));
			}
		}

		public void save(FileSystemMetaDataManager metaDataManager, File file) {
			metaDataManager.setProperty(ROOT_NAMESPACE_KEY, rootNamespace, file, false);
			System.out.println("********* Saving metadata for " + this);
			metaDataManager.saveMetaDataProperties(file);
		}

	}

	public <I> XMIMetaData getMetaData(FlexoResourceCenter<I> resourceCenter);

}
