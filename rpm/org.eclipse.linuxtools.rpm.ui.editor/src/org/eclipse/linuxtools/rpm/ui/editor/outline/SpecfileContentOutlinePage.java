package org.eclipse.linuxtools.rpm.ui.editor.outline;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.linuxtools.rpm.ui.editor.SpecfileEditor;
import org.eclipse.linuxtools.rpm.ui.editor.parser.SpecfileElement;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

public class SpecfileContentOutlinePage extends ContentOutlinePage {

	private ITextEditor editor;
	private IEditorInput input;
	
	public SpecfileContentOutlinePage(SpecfileEditor editor) {
		super();
		this.editor = editor;
	}

	public void setInput(IEditorInput editorInput) {
		this.input = editorInput;
		update();
	}

	public void update() {
		//set the input so that the outlines parse can be called
		//update the tree viewer state
		final TreeViewer viewer = getTreeViewer();

		if (viewer != null)
		{
			final Control control = viewer.getControl();
			if (control != null && !control.isDisposed())
			{
				control.getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (!control.isDisposed()) {
							control.setRedraw(false);
							if (input != null)
								viewer.setInput(input);
							viewer.expandAll();
							control.setRedraw(true);
						}
					}
				});
			}
		}
	}
	
	public void createControl(Composite parent) {

		super.createControl(parent);

		TreeViewer viewer= getTreeViewer();
		viewer.setContentProvider(new SpecfileContentProvider(editor));
		viewer.setLabelProvider(new SpecfileLabelProvider());
		viewer.addSelectionChangedListener(this);

		if (input != null)
			viewer.setInput(input);
	}
	
	/*
	 * Change in selection
	 */
	public void selectionChanged(SelectionChangedEvent event)
	{
		super.selectionChanged(event);
		
		//find out which item in tree viewer we have selected, and set highlight range accordingly
		ISelection selection = event.getSelection();
		if (selection.isEmpty())
			editor.resetHighlightRange();
		else
		{
			SpecfileElement element = (SpecfileElement) ((IStructuredSelection) selection)
					.getFirstElement();		
			
			int start = element.getLineStartPosition();
			int length = element.getLineEndPosition();
			try
			{
				editor.setHighlightRange(start, length, true);
			}
			catch (IllegalArgumentException x)
			{
				editor.resetHighlightRange();
			}
		}
	}

}
