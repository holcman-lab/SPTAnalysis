package fiji.plugin.SPTAnalysis.gui;

import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.scijava.ui.UIService;
import org.scijava.widget.FileWidget;

import fiji.plugin.SPTAnalysis.DataController;
import fiji.plugin.SPTAnalysis.SPTAnalysis;
import fiji.plugin.SPTAnalysis.readers.CSVReaderOptions;


public class InputPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private class LinkLabel extends JTextField implements MouseListener, FocusListener, ActionListener
	{
		private static final long serialVersionUID = 1L;

		private URI target;

		public Color standardColor = new Color(0,0,255);
		public Color hoverColor = new Color(255,0,0);
		public Color activeColor = new Color(128,0,128);


		private Border activeBorder;
		private Border hoverBorder;
		private Border standardBorder;

		public LinkLabel(URI target, String text)
		{
			super(text);
			this.target = target;

			setToolTipText(target.toString());
			setEditable(false);
			setForeground(standardColor);
			setBorder(standardBorder);
			setCursor(new Cursor(Cursor.HAND_CURSOR));

			this.addMouseListener(this);
			this.addFocusListener(this);
			this.addActionListener(this);
		}

		public void browse()
		{
			setForeground(activeColor);
			setBorder(activeBorder);
			try 
			{
				Desktop.getDesktop().browse(target);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			setForeground(standardColor);
			setBorder(standardBorder);
		}

		public void actionPerformed(ActionEvent ae)
		{
			browse();
		}

		public void mouseClicked(MouseEvent me)
		{
			browse();
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
		}

		@Override
		public void mouseEntered(MouseEvent me)
		{
			setForeground(hoverColor);
			setBorder(hoverBorder);
		}

		@Override
		public void mouseExited(MouseEvent me)
		{
			setForeground(standardColor);
			setBorder(standardBorder);
		}

		@Override
		public void focusLost(FocusEvent fe)
		{
			setForeground(standardColor);
			setBorder(standardBorder);
		}

		@Override
		public void focusGained(FocusEvent fe)
		{
			setForeground(hoverColor);
			setBorder(hoverBorder);
		}
	}

	private class ManualText extends JPanel
	{
		private static final long serialVersionUID = 1L;

		private GridBagLayout layout;

		public ManualText()
		{
			this.layout = new GridBagLayout();
			this.setLayout(layout);

			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridy = 0;
			c.insets = new Insets(10, 0, 0, 0);
	
			c.gridx = 0;
			c.anchor = GridBagConstraints.WEST;
			this.add(new JLabel("The manual can be found "), c);
	
			c.gridx = 1;
			c.insets = new Insets(10, 0, 0, 0);
			c.anchor = GridBagConstraints.WEST;
			try
			{
				this.add(new LinkLabel(new URI("https://docs.google.com/document/d/12a4hNXNEbJDkbb1czrA6oWm2YvU80d-FFRzxe6pkhtQ/edit?usp=sharing"),
						"here"), c);
			}
			catch (URISyntaxException e)
			{
				e.printStackTrace();
			}
		}
	}

	private final SPTAnalysis prog;
	private final UIService UIServ;
	final Frame myFrame;

	private CSVReaderOptionsPanel csvOptsPanel;
	private TimeWindowPanel timeWinPanel;
	private Double displayPxSize;

	private Choice inputFormatField;
	private HashMap<String, ArrayList<Component> > inputOptionsPanel;
	private String selected;
	private String selectedPath;
	private String fname;

	private GridBagLayout layout;

	private JTextArea introText;
	private JButton loadButton;

	public InputPanel(SPTAnalysis prog, UIService uis, final Frame myFrame)
	{
		this.prog = prog;
		this.UIServ = uis;
		this.myFrame = myFrame;

		this.introText = new JTextArea("The SPT Analysis plugin allows to:\n"
				+ "  * Display individual trajectories\n"
				+ "  * Construct maps (density, diffusion, drift)\n"
				+ "  * Detect potential wells\n"
				+ "  * Reconstruct a graph from the trajectories dynamics");
		this.introText.setBackground(this.myFrame.getBackground());

		this.csvOptsPanel = new CSVReaderOptionsPanel();
		this.timeWinPanel = new TimeWindowPanel();
		this.displayPxSize = 0.05;

		this.inputOptionsPanel = new HashMap<> ();
		this.inputOptionsPanel.put("Plugin", new ArrayList<> ());

		ArrayList<Component> tmp = new ArrayList<> ();
		tmp.add(this.timeWinPanel);
		tmp.add(new JSeparator(JSeparator.HORIZONTAL));
		this.inputOptionsPanel.put("Trackmate", tmp);

		tmp = new ArrayList<> ();
		tmp.add(this.timeWinPanel);
		tmp.add(new JSeparator(JSeparator.HORIZONTAL));
		tmp.add(this.csvOptsPanel);
		tmp.add(new JSeparator(JSeparator.HORIZONTAL));
		this.inputOptionsPanel.put("Custom", tmp);

		this.selected = "Plugin";
		this.selectedPath = null;
		this.fname = null;

		this.inputFormatField = new Choice();
		this.inputFormatField.add("Plugin");
		this.inputFormatField.add("Trackmate");
		this.inputFormatField.add("Custom");
		this.inputFormatField .addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				for (Component jp: inputOptionsPanel.get(selected))
					remove(jp);

				selected = inputFormatField.getSelectedItem();

				int cpt = 6;
				for (Component jp: inputOptionsPanel.get(selected))
				{
					{
						GridBagConstraints c = new GridBagConstraints();
						c.fill = GridBagConstraints.BOTH;
						c.gridwidth = GridBagConstraints.REMAINDER;
						c.weightx = 1;
						c.gridy = cpt;
						c.insets = new Insets(5, 10, 0, 10);
						add(jp, c);
					}
					++cpt;
				}

				myFrame.pack();
				revalidate();
				repaint();
			}
		});

		this.initGUI();
	}

	private void initGUI()
	{
		this.layout = new GridBagLayout();
		this.setLayout(layout);

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridy = 0;
			c.gridwidth = 2;
			c.insets = new Insets(10, 10, 0, 10);
			this.add(this.introText, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.gridy = 1;
			c.gridwidth = 2;
			c.anchor = GridBagConstraints.WEST;
			c.insets = new Insets(2, 10, 0, 10);
			this.add(new ManualText(), c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1;
			c.gridy = 2;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.insets = new Insets(5, 10, 0, 10);
			add(new JSeparator(JSeparator.HORIZONTAL), c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.5;
			c.gridy = 3;
			c.insets = new Insets(5, 10, 0, 0);
			this.add(new JLabel("Load from"), c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.5;
			c.gridy = 3;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.insets = new Insets(5, 0, 0, 10);
			this.add(this.inputFormatField, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.5;
			c.gridy = 4;
			c.insets = new Insets(5, 10, 0, 0);
			this.add(new JLabel("Choose file"), c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.5;
			c.gridy = 4;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.insets = new Insets(5, 0, 0, 10);
			JButton compute = new JButton("File");
			compute.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					File selFile = UIServ.chooseFile(null, FileWidget.OPEN_STYLE);
					if (selFile != null)
					{
						selectedPath = selFile.getAbsolutePath();
						fname = selFile.getName();
						loadButton.setEnabled(true);
					}
					else
						loadButton.setEnabled(false);
				}
			});
			this.add(compute, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1;
			c.gridy = 5;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.insets = new Insets(5, 10, 0, 10);
			this.add(new JSeparator(JSeparator.HORIZONTAL), c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.gridy = 10;
			c.insets = new Insets(5, 10, 0, 10);
			this.loadButton = new JButton("LOAD");
			this.loadButton.setEnabled(false);
			loadButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					if (inputFormatField.getSelectedItem().equals("Custom"))
						prog.loadRawTrajectories(selectedPath, csvOptsPanel.getOptions());
					else if (inputFormatField.getSelectedItem().equals("Trackmate"))
						prog.loadRawTrajectories(selectedPath, CSVReaderOptions.trackmateOptions());
					else if (inputFormatField.getSelectedItem().equals("Plugin"))
						prog.setDcntrl(DataController.controllerFromFile(selectedPath));
					else
						assert(false);
				}
			});
			this.add(this.loadButton, c);
		}

		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.weighty = 1;
			this.add(new JPanel(), c);
		}
	}

	public boolean useTimeWindow()
	{
		return this.timeWinPanel.useTimeWindow();
	}

	public double timeWindowDuration()
	{
		return this.timeWinPanel.timeWindowDuration();
	}

	public double timeWindowOverlap()
	{
		return this.timeWinPanel.timeWindowOverlap();
	}

	public double displayPxSize()
	{
		return this.displayPxSize;
	}

	public String fname()
	{
		return this.fname;
	}
}
