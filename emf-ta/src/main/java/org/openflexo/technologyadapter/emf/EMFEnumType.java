/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
 * 
 * This file is part of Flexo-foundation, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.emf;

import java.lang.reflect.Type;

import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.utils.FlexoObjectReference.ReferenceOwner;
import org.openflexo.technologyadapter.emf.metamodel.EMFClassClass;
import org.openflexo.technologyadapter.emf.metamodel.EMFEnumClass;
import org.openflexo.technologyadapter.emf.model.EMFObjectIndividual;
import org.openflexo.toolbox.StringUtils;

/**
 * An type defined as an {@link EMFObjectIndividual} of a given {@link EMFClassClass}
 *
 * @author sylvain
 *
 */
public class EMFEnumType extends IndividualOfClass<EMFTechnologyAdapter, EMFObjectIndividual, EMFEnumClass> {

	public static EMFEnumType getEMFObjectIndividualOfClass(EMFEnumClass anOntologyClass) {
		if (anOntologyClass == null) {
			return null;
		}
		return (EMFEnumType) anOntologyClass.getTechnologyAdapter().getTechnologyContextManager().getIndividualOfClass(anOntologyClass);
	}

	public static EMFEnumType UNDEFINED_EMF_INDIVIDUAL_TYPE = new EMFEnumType(null);

	/**
	 * Factory for {@link EMFEnumType} instances
	 * 
	 * @author sylvain
	 * 
	 */
	public static class EMFObjectIndividualTypeFactory extends
			IndividualOfClassTypeFactory<EMFTechnologyAdapter, EMFObjectIndividual, EMFEnumClass, EMFEnumType> implements ReferenceOwner {

		public EMFObjectIndividualTypeFactory(EMFTechnologyAdapter technologyAdapter) {
			super(technologyAdapter);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Class<EMFEnumType> getCustomType() {
			return EMFEnumType.class;
		}
	}

	public EMFEnumType(EMFEnumClass anOntologyClass) {
		super(anOntologyClass);
	}

	@Override
	public Class<EMFObjectIndividual> getBaseClass() {
		return EMFObjectIndividual.class;
	}

	@Override
	public boolean isTypeAssignableFrom(Type aType, boolean permissive) {
		// System.out.println("isTypeAssignableFrom " + aType + " (i am a " + this + ")");
		if (aType instanceof EMFEnumType) {
			return getOntologyClass().isSuperConceptOf(((EMFEnumType) aType).getOntologyClass());
		}
		if (aType instanceof EMFObjectIndividual) {
			// TODO: something better to do here !!!
			return true;
		}
		return false;
	}

	@Override
	public String simpleRepresentation() {
		if (getSpecificTypeInfo() != null && StringUtils.isNotEmpty(getSpecificTypeInfo().getSerializationForm())) {
			return getSpecificTypeInfo().getSerializationForm();
		}
		return getOntologyClass() != null ? getOntologyClass().getName() : "EMFObjectIndividual";
	}

	@Override
	public String fullQualifiedRepresentation() {
		if (getSpecificTypeInfo() != null && StringUtils.isNotEmpty(getSpecificTypeInfo().getSerializationForm())) {
			return getSpecificTypeInfo().getSerializationForm();
		}
		return getClass().getName() + "(" + getSerializationRepresentation() + ")";
	}

}
