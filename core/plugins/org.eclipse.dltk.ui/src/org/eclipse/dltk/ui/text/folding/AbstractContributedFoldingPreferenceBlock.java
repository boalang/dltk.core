package org.eclipse.dltk.ui.text.folding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.ui.preferences.ImprovedAbstractConfigurationBlock;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore;
import org.eclipse.dltk.ui.preferences.OverlayPreferenceStore.OverlayKey;
import org.eclipse.dltk.ui.preferences.PreferencesMessages;
import org.eclipse.dltk.ui.util.SWTFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

/**
 * Abstract base class that may be used to create
 * <code>IFoldingPreferenceBlock</code> implementations.
 */
public abstract class AbstractContributedFoldingPreferenceBlock extends
		ImprovedAbstractConfigurationBlock implements IFoldingPreferenceBlock {

	public AbstractContributedFoldingPreferenceBlock(
			OverlayPreferenceStore store, PreferencePage page) {
		super(store, page);
	}

	@Override
	public Control createControl(Composite parent) {
		Composite composite = SWTFactory.createComposite(parent,
				parent.getFont(), 1, 1, GridData.FILL);

		createOptionsControl(composite);

		SWTFactory.createHorizontalSpacer(composite, 1);

		Group initFoldGroup = SWTFactory.createGroup(composite,
				PreferencesMessages.FoldingConfigurationBlock_initiallyFold, 1,
				1, GridData.FILL_HORIZONTAL);

		addInitiallyFoldOptions(initFoldGroup);

		return composite;
	}

	/**
	 * Create language specific folding options
	 *
	 * @param composite
	 *            composite the option controls should be added to
	 */
	protected abstract void createOptionsControl(Composite composite);

	/**
	 * Adds the checkboxes that will be used to control 'initially fold'
	 * options.
	 *
	 * @param group
	 *            composite the checkboxes will be added to
	 */
	protected abstract void addInitiallyFoldOptions(Group group);

	/**
	 * Adds the folding option preference overlay keys.
	 */
	protected abstract void addOverlayKeys(List<OverlayKey> keys);

	@Override
	protected final List<OverlayKey> createOverlayKeys() {
		ArrayList<OverlayKey> keys = new ArrayList<>();
		addOverlayKeys(keys);
		return keys;
	}

	/**
	 * Convienence method to create and bind a checkbox control
	 *
	 * @param parent
	 *            parent composite
	 * @param text
	 *            checkbox text
	 * @param key
	 *            preference key
	 */
	protected Button createCheckBox(Composite parent, String text, String key) {
		Button button = SWTFactory.createCheckButton(parent, text, 1);
		bindControl(button, key);
		return button;
	}

	/**
	 * Convienence method to create and bind a radio button control
	 *
	 * @param parent
	 *            parent composite
	 * @param text
	 *            radio button text
	 * @param key
	 *            preference key
	 * @param value
	 *            value that will be saved to the preference store if the radio
	 *            button is enabled
	 */
	protected Button createRadioButton(Composite parent, String text,
			String key, Object value) {
		Button button = SWTFactory.createRadioButton(parent, text);
		bindControl(button, key, value);
		return button;
	}
}
