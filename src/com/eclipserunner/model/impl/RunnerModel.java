package com.eclipserunner.model.impl;

import static com.eclipserunner.Messages.Message_uncategorized;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.viewers.ITreeContentProvider;

import com.eclipserunner.model.ICategoryChangeListener;
import com.eclipserunner.model.ILaunchConfigurationCategory;
import com.eclipserunner.model.ILaunchConfigurationNode;
import com.eclipserunner.model.IModelChangeListener;
import com.eclipserunner.model.IRunnerModel;


/**
 * Class implementing {@link ITreeContentProvider} acts as a model for launch configuration tree.
 * By default provides "uncategorized" category.
 * 
 * @author vachacz
 */
public class RunnerModel implements IRunnerModel, ICategoryChangeListener {

	private static RunnerModel runnerModel = new RunnerModel();

	private List<IModelChangeListener> modelChangeListeners = new ArrayList<IModelChangeListener>();
	private Set<ILaunchConfigurationCategory> launchConfigurationCategories;

	private ILaunchConfigurationCategory uncategorizedCategory;

	protected RunnerModel() {
		uncategorizedCategory = new LaunchConfigurationCategory();
		uncategorizedCategory.setName(Message_uncategorized);
		uncategorizedCategory.addCategoryChangeListener(this);

		launchConfigurationCategories = new HashSet<ILaunchConfigurationCategory>();
		launchConfigurationCategories.add(uncategorizedCategory);
	}

	public static IRunnerModel getDefault() {
		return runnerModel;
	}

	public Set<ILaunchConfigurationCategory> getLaunchConfigurationCategories() {
		return launchConfigurationCategories;
	}

	public void addLaunchConfigurationNode(ILaunchConfigurationNode configuration) {
		uncategorizedCategory.add(configuration);
		// fireModelChangedEvent() not needed because category change triggers an event
	}

	public ILaunchConfigurationCategory addLaunchConfigurationCategory(String name) {
		ILaunchConfigurationCategory category = new LaunchConfigurationCategory();
		category.setName(name);
		category.addCategoryChangeListener(this);

		launchConfigurationCategories.add(category);
		fireModelChangedEvent();
		return category;
	}

	public void removeLaunchConfigurationNode(ILaunchConfigurationNode configuration) {
		for (ILaunchConfigurationCategory launchConfigurationCategory : launchConfigurationCategories) {
			launchConfigurationCategory.remove(configuration);
		}
		fireModelChangedEvent();
	}

	public void removeLaunchConfiguration(ILaunchConfiguration configuration) {
		for (ILaunchConfigurationCategory category : launchConfigurationCategories) {
			category.remove(configuration);
		}
	}

	public ILaunchConfigurationCategory getUncategorizedCategory() {
		return uncategorizedCategory;
	}

	public void removeLaunchConfigurationCategory(ILaunchConfigurationCategory category) {
		// TODO/FIXME: BARY LWA java.util.ConcurrentModificationException
		//for(ILaunchConfiguration launchConfiguration : category.getLaunchConfigurationSet()) {
		//	category.remove(launchConfiguration);
		//	uncategorizedCategory.add(launchConfiguration);
		//}
		launchConfigurationCategories.remove(category);
		category.removeCategoryChangeListener(this);
		fireModelChangedEvent();
	}

	public ILaunchConfigurationCategory getLaunchConfigurationCategory(String name) {
		for (ILaunchConfigurationCategory launchConfigurationCategory : launchConfigurationCategories) {
			if (launchConfigurationCategory.getName().equals(name)) {
				return launchConfigurationCategory;
			}
		}
		return null;
	}

	public void addModelChangeListener(IModelChangeListener listener) {
		modelChangeListeners.add(listener);
	}

	public void removeModelChangeListener(IModelChangeListener listener) {
		modelChangeListeners.remove(listener);
	}

	private void fireModelChangedEvent() {
		for (IModelChangeListener listener : modelChangeListeners) {
			listener.modelChanged();
		}
	}

	// for test only
	protected void setLaunchConfigurationCategories(Set<ILaunchConfigurationCategory> launchConfigurationCategories) {
		this.launchConfigurationCategories = launchConfigurationCategories;
	}

	public ILaunchConfigurationNode findLaunchConfigurationNodeBy(ILaunchConfiguration configuration) {
		for (ILaunchConfigurationCategory launchConfigurationCategory : launchConfigurationCategories) {
			// TODO LWA BARY: maybe category.contains() could check also ILaunchConfigs ??
			for (ILaunchConfigurationNode launchConfigurationNode : launchConfigurationCategory.getLaunchConfigurationNodes()) {
				if (launchConfigurationNode.getLaunchConfiguration().equals(configuration)) {
					return launchConfigurationNode;
				}
			}
		}
		return null;
	}

	public void categoryChanged() {
		fireModelChangedEvent();
	}

}