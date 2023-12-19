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

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.openflexo.foundation.resource.FlexoIODelegate;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.Import;
import org.openflexo.pamela.annotations.Imports;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;

/**
 * Represents an abstract EMF metamodel resource
 * 
 * @author sylvain
 *
 */
@ModelEntity(isAbstract = true)
@Imports({ @Import(ECoreMetaModelResource.class), @Import(JarBasedMetaModelResource.class) })
public interface EMFMetaModelResource extends FlexoMetaModelResource<EMFModel, EMFMetaModel, EMFTechnologyAdapter>,
		TechnologyAdapterResource<EMFMetaModel, EMFTechnologyAdapter> {

	public static final String PACKAGE = "package";

	/**
	 * Return the effective metamodel addressed by this resource, its {@link ResourceData}
	 * 
	 * @return
	 */
	public EMFMetaModel getMetaModel();

	/**
	 * Return the effective metamodel addressed by this resource, its {@link ResourceData}
	 * 
	 * @return
	 */
	@Override
	public EMFMetaModel getMetaModelData();

	/**
	 * Creates a new ModelResource, for EMF, MetaModel decides wich type of serialization you should use!
	 * 
	 * @param flexoIODelegate
	 * @return
	 */
	Resource createEMFModelResource(FlexoIODelegate<?> flexoIODelegate);

	/**
	 * Getter of Package MetaModel.
	 * 
	 * @return
	 */
	@Getter(value = PACKAGE, ignoreType = true)
	EPackage getPackage();

	/**
	 * Setter of Package MetaModel.
	 * 
	 * @param ePackage
	 */
	@Setter(value = PACKAGE)
	void setPackage(EPackage ePackage);

}
