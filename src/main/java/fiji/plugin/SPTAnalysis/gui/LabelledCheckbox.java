package fiji.plugin.SPTAnalysis.gui;

import java.awt.Checkbox;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class LabelledCheckbox extends JPanel
{
	private static final long serialVersionUID = 1L;

	public JLabel nameLabel;
	public Checkbox cb;

	public LabelledCheckbox(String name)
	{
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);

		this.nameLabel = new JLabel(name);
		this.cb = new Checkbox("", false);

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.4;
			c.weighty = 0.0;
			c.gridy = 0;
			this.add(this.nameLabel, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.6;
			c.weighty = 0.0;
			c.gridy = 0;
			c.gridwidth = GridBagConstraints.REMAINDER;
			this.add(this.cb, c);
		}
	}
}
