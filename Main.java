import java.awt.*;
import java.util.*;
import java.lang.*;
import java.time.*;
import javax.swing.*;
import java.awt.event.*;

enum Verdict 
{
        AC,
        WA,
        TLE,
        MLE,
        PE
}

class Submission
{
        int submissionID;
        int problemID;
        Verdict verdict;
        Instant submissionTime;
}

class SubmissionList
{
        ArrayList < Submission > list;
}

class Problem
{
        int ProblemID;
        boolean isSolved;
        int lastSubmissionID;
}

class ProblemList
{
        ArrayList < Problem > list;
}

class Contest
{
        int score;
        int numberOfProblems;
        boolean isContestActive;
        String teamName;
        String institution;
        Duration timePenalty;
        Duration contestDuration;
        ProblemList problems;
        SubmissionList submissions;

        Contest(int numberOfProblems, String teamName, String institution)
        {
                this.score = 0;
                this.numberOfProblems = numberOfProblems;
                this.isContestActive = true;
                this.teamName = new String(teamName);
                this.institution = new String(institution);
                this.problems = new ProblemList();
                this.submissions = new SubmissionList();
        }
}

class ContestWindow extends JFrame
{
        Contest contest;

        public static void changeFont(Component component, Font font)
        {
                component.setFont(font);
                if (component instanceof Container) 
                {
                        for (Component child: ((Container) component).getComponents())
                        {
                                changeFont(child, font);
                        }
                }
        }

        Contest showContestSetupDialog()
        {
                Contest newContest = null;

                JPanel panel = new JPanel();
                
                JLabel problemsLabel = new JLabel("Total Problems (A - ?) :");
                JComboBox problemsComboBox = new JComboBox("ABCDEFGHIJKLMOPQRSTUVWXYZ".split(""));
                problemsComboBox.setEditable(false);
                
                JLabel teamNameLabel = new JLabel("Team Name :");
                JTextField teamNameTextField = new JTextField("MyTeam", 15);
                teamNameTextField.addFocusListener(new FocusListener()
                {
                        @Override
                        public void focusGained(FocusEvent e)
                        {
                                if (teamNameTextField.getText().equals("MyTeam"))
                                {
                                        teamNameTextField.setText("");
                                }
                        }
                        
                        @Override
                        public void focusLost(FocusEvent e)
                        {
                                if (teamNameTextField.getText().equals(""))
                                {
                                        teamNameTextField.setText("MyTeam");
                                }
                        }
                });
                
                JLabel InstitutionLabel = new JLabel("Institution Name :");
                JTextField institutionTextField = new JTextField("MyInstitution", 15);
                institutionTextField.addFocusListener(new FocusListener()
                {
                        @Override
                        public void focusGained(FocusEvent e)
                        {
                                if (institutionTextField.getText().equals("MyInstitution"))
                                {
                                        institutionTextField.setText("");
                                }
                        }
                        
                        @Override
                        public void focusLost(FocusEvent e)
                        {
                                if (institutionTextField.getText().length() == 0)
                                {
                                        institutionTextField.setText("MyInstitution");
                                }
                        }
                });
                
                panel.setLayout(new GridLayout(3, 2));
                panel.add(problemsLabel);
                panel.add(problemsComboBox);
                panel.add(teamNameLabel);
                panel.add(teamNameTextField);
                panel.add(InstitutionLabel);
                panel.add(institutionTextField);

                int result = JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Start New Contest",
                        JOptionPane.OK_CANCEL_OPTION
                );
                if (result == JOptionPane.OK_OPTION)
                {
                        int numberOfProblems = ((String) problemsComboBox.getSelectedItem()).charAt(0) - 'A' + 1;
                        String teamName = teamNameTextField.getText();
                        String institution = institutionTextField.getText();
                        
                        newContest = new Contest(numberOfProblems, teamName, institution);
                }
                else
                {
                        System.exit(0);
                }

                return newContest;
        }

        ContestWindow()
        {
                contest = showContestSetupDialog();
                setTitle("Contest - " + contest.teamName);

                setSize(960, 540);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setVisible(true);
        }
}

public class Main
{
        public static void main(String args[])
        {
                ContestWindow app = new ContestWindow();
                return;
        }
}
