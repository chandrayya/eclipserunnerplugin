package com.eclipserunner.model.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IViewSite;

import com.eclipserunner.model.ICategoryNode;
import com.eclipserunner.model.ILaunchNode;
import com.eclipserunner.model.IRunnerModel;
import com.eclipserunner.model.impl.LaunchTypeNode;

/**
 * Development code ... before refactoring.
 *
 * @author vachacz
 */
public class RunnerModelTreeWithTypesAdapter implements ITreeContentProvider {

	private IRunnerModel runnerModel;

	public RunnerModelTreeWithTypesAdapter(IRunnerModel runnerModel) {
		this.runnerModel = runnerModel;
	}

	public Object[] getChildren(Object object) {
		if (object instanceof ICategoryNode) {
			return getChildrenByCategoryNode((ICategoryNode) object);
		}
		else if (object instanceof LaunchTypeNode) {
			return getChildrenByLaunchTypeNode((LaunchTypeNode) object);
		}
		return null;
	}

	public Object getParent(Object object) {
		if (object instanceof ILaunchNode) {
			return getParentByLaunchNode((ILaunchNode) object);
		}
		else if (object instanceof LaunchTypeNode) {
			return ((LaunchTypeNode) object).getCategoryNode();
		}
		return null;
	}

	public boolean hasChildren(Object parent) {
		if (parent instanceof ICategoryNode) {
			ICategoryNode categoryNode = (ICategoryNode) parent;
			return !categoryNode.isEmpty();
		}
		else if (parent instanceof LaunchTypeNode) {
			return true;
		}
		return false;
	}

	// TODO LWA make it better
	public Object[] getElements(Object parent) {
		if (parent instanceof IViewSite) {
			Collection<ICategoryNode> categoryNodes = runnerModel.getCategoryNodes();
			Object[] objects = categoryNodes.toArray();
			if (! runnerModel.isDefaultCategoryNodeVisible()) {
				objects = Arrays.asList(objects).subList(1, objects.length).toArray();
			}
			return objects;
		}
		return getChildren(parent);
	}

	// TODO LWA make it better and easier
	private Object getParentByLaunchNode(ILaunchNode launchNode) {
		ILaunchConfigurationType type;
		try {
			type = launchNode.getLaunchConfiguration().getType();
		} catch (CoreException e) {
			return null;
		}
		Object[] children = getChildren(launchNode.getCategoryNode());
		for (Object child : children) {
			if (((LaunchTypeNode) child).getLaunchConfigurationType() == type) {
				return child;
			}
		}
		return null;
	}

	private Object[] getChildrenByLaunchTypeNode(LaunchTypeNode launchTypeNode) {
		ICategoryNode categoryNode = launchTypeNode.getCategoryNode();

		List<ILaunchNode> launchNodes = new ArrayList<ILaunchNode>();
		for (ILaunchNode launchNode : categoryNode.getLaunchNodes()) {
			try {
				if (launchTypeNode.getLaunchConfigurationType().equals(launchNode.getLaunchConfiguration().getType())) {
					launchNodes.add(launchNode);
				}
			} catch (CoreException e) {
			}
		}
		return launchNodes.toArray();
	}

	private Object[] getChildrenByCategoryNode(ICategoryNode categoryNode) {
		List<LaunchTypeNode> launchTypeNodes = new ArrayList<LaunchTypeNode>();
		for (ILaunchNode launchNode : categoryNode.getLaunchNodes()) {
			try {
				ILaunchConfigurationType type = launchNode.getLaunchConfiguration().getType();
				if (!isTypeOnTheList(type, launchTypeNodes)) {
					LaunchTypeNode launchNodeType = new LaunchTypeNode();
					launchNodeType.setCategoryNode(categoryNode);
					launchNodeType.setLaunchConfigurationType(type);

					launchTypeNodes.add(launchNodeType);
				}
			} catch (CoreException e) {
			}
		}
		return launchTypeNodes.toArray();
	}

	private boolean isTypeOnTheList(ILaunchConfigurationType type,	List<LaunchTypeNode> launchTypeNodes) {
		for (LaunchTypeNode launchTypeNode : launchTypeNodes) {
			if (launchTypeNode.getLaunchConfigurationType().equals(type)) {
				return true;
			}
		}
		return false;
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
