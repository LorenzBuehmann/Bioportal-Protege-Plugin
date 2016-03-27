package de.leipzig.imise.bioportal;

import java.awt.BorderLayout;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.view.View;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.core.ui.view.ViewsPane;
import org.protege.editor.core.ui.workspace.TabbedWorkspace;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.core.ui.workspace.WorkspaceTab;
import org.protege.editor.core.ui.workspace.WorkspaceViewsTab;
import org.protege.editor.owl.ui.view.AbstractOWLSelectionViewComponent;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

import de.leipzig.imise.bioportal.ui.SearchPanel;

public class BioportalViewComponent extends AbstractOWLViewComponent {
    private static final long serialVersionUID = -4515710047558710080L;
    
    private static final Logger log = Logger.getLogger(BioportalViewComponent.class);
    

    @Override
    protected void disposeOWLView() {
    	
    }

    @Override
    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());
        add(new SearchPanel(getOWLEditorKit()));
        ViewComponentPlugin viewPlugin = getWorkspace().getViewManager().getViewComponentPlugin("org.protege.editor.owl.OWLAssertedClassHierarchy");
        log.info(viewPlugin);
        log.info(viewPlugin.getId());
        log.info(viewPlugin.getLabel());

        String viewId = viewPlugin.getId();
        Workspace workspace = viewPlugin.getWorkspace();
		if (workspace instanceof TabbedWorkspace) {
			WorkspaceTab tab = ((TabbedWorkspace) workspace).getSelectedTab();
            log.info(tab.getLabel());
			if (tab instanceof WorkspaceViewsTab) {
				ViewsPane viewPane = ((WorkspaceViewsTab) tab).getViewsPane();
                log.info(viewPane.getViews());
				for (View view : viewPane.getViews()) {
                    log.info("View ID:" + view.getId());
					if (view.getId() != null && view.getId().equals(viewId)) {
                        log.info("YES");
					}
				}
			}
		}

        View view = getWorkspace().getViewManager().showView("OWLAssertedClassHierarchy");
        System.out.println(view);
        log.info(view);
        log.info(view.getId());

        AbstractOWLSelectionViewComponent hc = (AbstractOWLSelectionViewComponent) view.getViewComponent();
        log.info(hc.getClass());
        Plugin p = new Plugin();
        log.info("BioPortal View Component initialized");
    }
}