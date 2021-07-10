/**
 * 
 * Copyright (c) 2014-2015, Openflexo
 * 
 * This file is part of Excelconnector, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.emf.fml.editionaction;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.foundation.fml.annotations.FML;
import org.openflexo.foundation.fml.editionaction.EditionAction;
import org.openflexo.foundation.fml.rt.RunTimeEvaluationContext;
import org.openflexo.pamela.annotations.Getter;
import org.openflexo.pamela.annotations.ImplementationClass;
import org.openflexo.pamela.annotations.ModelEntity;
import org.openflexo.pamela.annotations.PropertyIdentifier;
import org.openflexo.pamela.annotations.Setter;
import org.openflexo.pamela.annotations.XMLAttribute;
import org.openflexo.pamela.annotations.XMLElement;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;

/**
 * {@link EditionAction} allowing to duplicate a {@link EMFModelResource}
 * 
 * @author sylvain
 *
 */
@ModelEntity
@ImplementationClass(DuplicateEMFModel.DuplicateEMFModelImpl.class)
@XMLElement
@FML("DuplicateEMFModel")
public interface DuplicateEMFModel extends AbstractCreateEMFResource {

	@PropertyIdentifier(type = DataBinding.class)
	public static final String COPIED_EMF_MODEL_KEY = "copiedEMFModel";

	@Getter(value = COPIED_EMF_MODEL_KEY)
	@XMLAttribute
	public DataBinding<EMFModel> getCopiedEMFModel();

	@Setter(COPIED_EMF_MODEL_KEY)
	public void setCopiedEMFModel(DataBinding<EMFModel> value);

	public static abstract class DuplicateEMFModelImpl extends AbstractCreateEMFResourceImpl implements DuplicateEMFModel {

		private static final Logger logger = Logger.getLogger(DuplicateEMFModel.class.getPackage().getName());

		private DataBinding<EMFModel> copiedEMFModel;

		@Override
		public DataBinding<EMFModel> getCopiedEMFModel() {
			if (copiedEMFModel == null) {
				copiedEMFModel = new DataBinding<>(this, EMFModel.class, BindingDefinitionType.GET);
				copiedEMFModel.setBindingName("copiedEMFModel");
			}
			return copiedEMFModel;
		}

		@Override
		public void setCopiedEMFModel(DataBinding<EMFModel> value) {
			if (value != null) {
				value.setOwner(this);
				value.setBindingName("copiedEMFModel");
				value.setDeclaredType(EMFModel.class);
				value.setBindingDefinitionType(BindingDefinitionType.GET);
			}
			this.copiedEMFModel = value;
		}

		@Override
		protected EMFModelResource getInitialResource(RunTimeEvaluationContext evaluationContext) {
			if (getCopiedEMFModel() != null) {
				try {
					EMFModel copiedModel = getCopiedEMFModel().getBindingValue(evaluationContext);
					if (copiedModel != null) {
						return copiedModel.getResource();
					}
				} catch (TypeMismatchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NullReferenceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

	}
}
