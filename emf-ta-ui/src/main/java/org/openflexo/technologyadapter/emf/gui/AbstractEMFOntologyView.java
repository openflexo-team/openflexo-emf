/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Openflexo-technology-adapters-ui, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.emf.gui;

import javax.swing.ImageIcon;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.ontology.IFlexoOntology;
import org.openflexo.ontology.components.widget.OntologyView;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;

/**
 * This class represent the module view for an OWL ontology.<br>
 * Underlying representation is supported by OntologyView implementation.
 * 
 * @author sylvain
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractEMFOntologyView<T extends FlexoObject & IFlexoOntology<EMFTechnologyAdapter>>
		extends OntologyView<T, EMFTechnologyAdapter> {

	public AbstractEMFOntologyView(T object, FlexoController controller, FlexoPerspective perspective) {
		super(object, controller, perspective,
				controller != null ? controller.getTechnologyAdapter(EMFTechnologyAdapter.class).getLocales() : null);
	}

	@Override
	public ImageIcon getOntologyClassIcon() {
		return EMFIconLibrary.EMF_CLASS_ICON;
	}

	@Override
	public ImageIcon getOntologyIndividualIcon() {
		return EMFIconLibrary.EMF_INDIVIDUAL_ICON;
	}

	@Override
	public ImageIcon getOntologyDataPropertyIcon() {
		return EMFIconLibrary.EMF_ATTRIBUTE_ICON;
	}

	@Override
	public ImageIcon getOntologyObjectPropertyIcon() {
		return EMFIconLibrary.EMF_ATTRIBUTE_ICON;
	}

	@Override
	public ImageIcon getOntologyAnnotationIcon() {
		return EMFIconLibrary.EMF_ATTRIBUTE_ICON;
	}

	@Override
	public boolean supportTechnologySpecificHiddenConcepts() {
		return false;
	}

	@Override
	public String technologySpecificHiddenConceptsLabel() {
		// Not applicable
		return null;
	}

}