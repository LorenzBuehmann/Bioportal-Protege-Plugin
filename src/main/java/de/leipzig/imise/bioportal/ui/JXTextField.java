package de.leipzig.imise.bioportal.ui;

import java.awt.Color;

import javax.swing.JTextField;

import org.jdesktop.swingx.prompt.PromptSupport;

/**
 * {@link JTextField}, with integrated support for prompts and buddies.
 *
 * @see PromptSupport
 * @author Peter Weishapl <petw@gmx.net>
 *
 */
public class JXTextField extends JTextField {
        public JXTextField() {
                this(null);
        }

        public JXTextField(String promptText) {
                this(promptText, null);
        }

        public JXTextField(String promptText, Color promptForeground) {
                this(promptText, promptForeground, null);
        }

        public JXTextField(String promptText, Color promptForeground,
                        Color promptBackground) {
                PromptSupport.init(promptText, promptForeground, promptBackground,
                                   this);
        }

        /**
         * @see PromptSupport#getFocusBehavior(javax.swing.text.JTextComponent)
         */
        public PromptSupport.FocusBehavior getFocusBehavior() {
                return PromptSupport.getFocusBehavior(this);
        }

        /**
         * @see PromptSupport#getPrompt(javax.swing.text.JTextComponent)
         */
        public String getPrompt() {
                return PromptSupport.getPrompt(this);
        }

        /**
         * @see PromptSupport#getForeground(javax.swing.text.JTextComponent)
         */
        public Color getPromptForeground() {
                return PromptSupport.getForeground(this);
        }

        /**
         * @see PromptSupport#getForeground(javax.swing.text.JTextComponent)
         */
        public Color getPromptBackground() {
                return PromptSupport.getBackground(this);
        }

        /**
         * @see PromptSupport#getFontStyle(javax.swing.text.JTextComponent)
         */
        public Integer getPromptFontStyle() {
                return PromptSupport.getFontStyle(this);
        }

        /**
         * @see PromptSupport#getFocusBehavior(javax.swing.text.JTextComponent)
         */
        public void setFocusBehavior(PromptSupport.FocusBehavior focusBehavior) {
                PromptSupport.setFocusBehavior(focusBehavior, this);
        }

        /**
         * @see PromptSupport#setPrompt(String, javax.swing.text.JTextComponent)
         */
        public void setPrompt(String labelText) {
                PromptSupport.setPrompt(labelText, this);
        }

        /**
         * @see PromptSupport#setForeground(Color, javax.swing.text.JTextComponent)
         */
        public void setPromptForeground(Color promptTextColor) {
                PromptSupport.setForeground(promptTextColor, this);
        }

        /**
         * @see PromptSupport#setBackground(Color, javax.swing.text.JTextComponent)
         */
        public void setPromptBackround(Color promptTextColor) {
                PromptSupport.setBackground(promptTextColor, this);
        }

        /**
         * @see PromptSupport#setFontStyle(Integer, javax.swing.text.JTextComponent)
         */
        public void setPromptFontStyle(Integer fontStyle) {
                PromptSupport.setFontStyle(fontStyle, this);
        }

}

