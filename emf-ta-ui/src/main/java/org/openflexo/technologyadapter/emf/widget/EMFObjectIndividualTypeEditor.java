/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexo-ui, a component of the software infrastructure 
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

package org.openflexo.technologyadapter.emf.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.openflexo.ApplicationContext;
import org.openflexo.components.widget.DefaultCustomTypeEditorImpl;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.fml.FlexoConceptInstanceType;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.gina.annotation.FIBPanel;
import org.openflexo.gina.model.widget.FIBCustom.FIBCustomComponent.CustomComponentParameter;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.rm.Resource;
import org.openflexo.technologyadapter.emf.EMFObjectIndividualType;
import org.openflexo.technologyadapter.emf.EMFTechnologyAdapter;
import org.openflexo.technologyadapter.emf.controller.EMFAdapterController;
import org.openflexo.technologyadapter.emf.gui.EMFMetaModelBrowserModel;
import org.openflexo.technologyadapter.emf.metamodel.EMFClassClass;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.toolbox.StringUtils;

/**
 * An editor to edit a {@link FlexoConceptInstanceType}
 * 
 * @author sylvain
 * 
 */
@FIBPanel("Fib/CustomType/EMFObjectIndividualTypeEditor.fib")
public class EMFObjectIndividualTypeEditor extends DefaultCustomTypeEditorImpl<EMFObjectIndividualType> {

	static final Logger logger = Logger.getLogger(EMFObjectIndividualTypeEditor.class.getPackage().getName());

	private EMFClassClass selectedEMFClass = null;

	protected EMFMetaModelBrowserModel browserModel = null;

	private EMFMetaModelResource metaModelResource;

	private String filteredClassName = "";
	// private boolean searchMode = false;
	private List<EMFClassClass> matchingValues = new ArrayList<>();

	public EMFObjectIndividualTypeEditor(FlexoServiceManager serviceManager) {
		super(serviceManager);
	}

	@Override
	public String getPresentationName() {
		return "EMF instance";
	}

	@Override
	public Class<EMFObjectIndividualType> getCustomType() {
		return EMFObjectIndividualType.class;
	}

	public EMFMetaModelBrowserModel getBrowserModel() {
		return browserModel;
	}

	public void setBrowserModel(EMFMetaModelBrowserModel browserModel) {
		if ((browserModel == null && this.browserModel != null) || (browserModel != null && !browserModel.equals(this.browserModel))) {
			EMFMetaModelBrowserModel oldValue = this.browserModel;
			this.browserModel = browserModel;
			getPropertyChangeSupport().firePropertyChange("browserModel", oldValue, browserModel);
		}
	}

	public EMFMetaModelResource getMetaModelResource() {
		return metaModelResource;
	}

	public void setMetaModelResource(EMFMetaModelResource metaModelResource) {
		if ((metaModelResource == null && this.metaModelResource != null)
				|| (metaModelResource != null && !metaModelResource.equals(this.metaModelResource))) {
			EMFMetaModelResource oldValue = this.metaModelResource;
			this.metaModelResource = metaModelResource;
			getPropertyChangeSupport().firePropertyChange("modelResource", oldValue, metaModelResource);
			browserModel = makeBrowserModel(metaModelResource != null ? metaModelResource.getMetaModel() : null);
			getPropertyChangeSupport().firePropertyChange("browserModel", null, getBrowserModel());
		}
	}

	public EMFClassClass getSelectedClass() {
		return selectedEMFClass;
	}

	public void setSelectedClass(EMFClassClass selectedClass) {
		if ((selectedClass == null && this.selectedEMFClass != null)
				|| (selectedClass != null && !selectedClass.equals(this.selectedEMFClass))) {
			EMFClassClass oldValue = this.selectedEMFClass;
			this.selectedEMFClass = selectedClass;
			getPropertyChangeSupport().firePropertyChange("selectedClass", oldValue, selectedClass);
			matchingValues.clear();
			getPropertyChangeSupport().firePropertyChange("matchingValues", null, matchingValues);
			getPropertyChangeSupport().firePropertyChange("searchLabel", null, getSearchLabel());
		}
	}

	@Override
	public EMFObjectIndividualType getEditedType() {
		return EMFObjectIndividualType.getEMFObjectIndividualOfClass(getSelectedClass());
	}

	public List<EMFClassClass> getMatchingValues() {
		return matchingValues;
	}

	public EMFTechnologyAdapter getEMFTechnologyAdapter() {
		if (getServiceManager() != null) {
			return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(EMFTechnologyAdapter.class);
		}
		return null;
	}

	private EMFModel emfModel;

	public EMFModel getEMFModel() {
		return emfModel;
	}

	@CustomComponentParameter(name = "EMFModel", type = CustomComponentParameter.Type.OPTIONAL)
	public void setEMFModel(EMFModel emfModel) {

		if (this.emfModel != emfModel) {
			FlexoObject oldRoot = getRootObject();
			this.emfModel = emfModel;
			getPropertyChangeSupport().firePropertyChange("rootObject", oldRoot, getRootObject());
		}
	}

	public FlexoObject getRootObject() {
		if (getEMFModel() != null) {
			return getEMFModel();
		}
		else {
			// return getEMFTechnologyAdapter();
			return null;
		}
	}

	@Override
	public Resource getFIBComponentResource() {
		// TODO Auto-generated method stub
		Resource returned = super.getFIBComponentResource();
		System.out.println("Cool on retourne " + returned);
		return returned;
	}

	public ImageIcon getSearchIcon() {
		return UtilsIconLibrary.SEARCH_ICON;
	}

	public ImageIcon getDoneIcon() {
		return UtilsIconLibrary.CANCEL_ICON;
	}

	public EMFTechnologyAdapter getTechnologyAdapter() {
		return getServiceManager().getTechnologyAdapterService().getTechnologyAdapter(EMFTechnologyAdapter.class);
	}

	public String getSearchLabel() {
		if (matchingValues.size() >= 1) {
			return "Found " + matchingValues.size() + " classes";
		}
		if (StringUtils.isNotEmpty(getFilteredClassName())) {
			return "No matches";
		}
		return "You might use wildcards (* = any string) and press 'Search'";

	}

	/**
	 * Build browser model<br>
	 * Override this method when required
	 * 
	 * @return
	 */
	protected EMFMetaModelBrowserModel makeBrowserModel(EMFMetaModel ontology) {

		EMFAdapterController emfTAC = ((ApplicationContext) getServiceManager()).getTechnologyAdapterControllerService()
				.getTechnologyAdapterController(getTechnologyAdapter());
		EMFMetaModelBrowserModel returned = (EMFMetaModelBrowserModel) emfTAC.makeOntologyBrowserModel(ontology);
		returned.disableAutoUpdate();
		returned.setStrictMode(false);
		returned.setHierarchicalMode(true);
		returned.setDisplayPropertiesInClasses(false);
		returned.setShowClasses(true);
		returned.setShowIndividuals(false);
		returned.setShowObjectProperties(false);
		returned.setShowDataProperties(false);
		returned.setShowAnnotationProperties(false);
		returned.enableAutoUpdate();
		returned.recomputeStructure();
		return returned;
	}

	public String getFilteredClassName() {
		return filteredClassName;
	}

	public void setFilteredClassName(String filteredClassName) {
		if (filteredClassName == null || !filteredClassName.equals(this.filteredClassName)) {
			String oldValue = this.filteredClassName;
			this.filteredClassName = filteredClassName;
			updateMatchingClasses();
			getPropertyChangeSupport().firePropertyChange("filteredClassName", oldValue, filteredClassName);
			/*if (searchMode) {
				updateMatchingClasses();
				getPropertyChangeSupport().firePropertyChange("searchMode", !searchMode(), searchMode());
			}*/
		}
	}

	/*public void search() {
		// LOGGER.info("SEARCH " + filteredClassName);
		// isExplicitelySearching = true;
		// explicitelySearch();
		updateMatchingClasses();
		// isExplicitelySearching = false;
		if (getMatchingValues().size() != 1) {
			searchMode = true;
		}
		getPropertyChangeSupport().firePropertyChange("searchMode", !searchMode(), searchMode());
	}
	
	public void done() {
		logger.info("Done with SEARCH " + filteredClassName);
		searchMode = false;
		setFilteredClassName("");
		getPropertyChangeSupport().firePropertyChange("searchMode", !searchMode(), searchMode());
	}
	
	public boolean searchMode() {
		return getMatchingValues().size() != 1 && searchMode;
	}*/

	private void updateMatchingClasses() {

		logger.info("*************** updateMatchingClasses() for " + filteredClassName);

		final List<EMFClassClass> oldMatchingValues = new ArrayList<>(getMatchingValues());
		// System.out.println("updateMatchingValues() with " + getFilteredName());
		matchingValues.clear();

		if (StringUtils.isNotEmpty(getFilteredClassName())) {
			if (getAllSelectableValues() != null && getFilteredClassName() != null) {
				for (IFlexoOntologyClass<EMFTechnologyAdapter> next : getAllSelectableValues()) {
					if (next instanceof EMFClassClass) {
						if (matches((EMFClassClass) next, getFilteredClassName())) {
							matchingValues.add((EMFClassClass) next);
						}
					}
				}
			}
		}
		logger.info("Objects matching with " + getFilteredClassName() + " found " + matchingValues.size() + " values");
		getPropertyChangeSupport().firePropertyChange("searchLabel", null, getSearchLabel());

		SwingUtilities.invokeLater(() -> {
			getPropertyChangeSupport().firePropertyChange("matchingValues", oldMatchingValues, getMatchingValues());
			if (matchingValues.size() == 1) {
				setSelectedClass(matchingValues.get(0));
			}
		});

	}

	public List<? extends IFlexoOntologyClass<EMFTechnologyAdapter>> getAllSelectableValues() {
		if (getMetaModelResource() != null) {
			return getMetaModelResource().getMetaModel().getClasses();
		}
		return null;
	}

	protected boolean matches(EMFClassClass o, String filteredName) {
		return o != null && StringUtils.isNotEmpty(o.getName()) && (o.getName()).toUpperCase().indexOf(filteredName.toUpperCase()) > -1;
	}

}
