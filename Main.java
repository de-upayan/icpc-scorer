import java.awt.*;
import java.util.*;
import java.lang.*;
import java.time.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;

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

class Problem
{
        int problemID;
        int numberOfAttempts;
        String problemSymbol;
        boolean isSolved;
        Instant solveTime;
        
        Problem (
                int problemID,
                String problemSymbol
        )
        {
                this.problemID = problemID;
                this.numberOfAttempts = 0;
                this.problemSymbol = problemSymbol;
                this.isSolved = false;
                this.solveTime = null;
        }
}

class Contest
{
        int score;
        final int numberOfProblems;
        boolean isContestActive;
        final String teamName;
        final String institution;
        Duration timePenalty;
        final Duration penaltyPerNonAC;
        final Duration contestDuration;
        Instant startTime;
        Problem[] problems;
        ArrayList < Submission > submissions;

        Contest(
                int numberOfProblems,
                String teamName,
                String institution,
                Duration penaltyPerNonAC,
                Duration contestDuration
        )
        {
                this.score = 0;
                this.numberOfProblems = numberOfProblems;
                this.isContestActive = true;
                this.teamName = new String(teamName);
                this.institution = new String(institution);
                this.timePenalty = Duration.ZERO;
                this.penaltyPerNonAC = penaltyPerNonAC;
                this.contestDuration = contestDuration;
                this.startTime = Instant.now();
                
                this.problems = new Problem[numberOfProblems];
                for (int i = 0; i < numberOfProblems; i++)
                {
                        problems[i] = new Problem(i, Character.toString('A' + i));
                }
                
                this.submissions = new ArrayList < Submission > ();
        }
}

class ContestWindow extends JFrame
{
        Contest contest;
        
        // source: https://stackoverflow.com/a/12731354
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
        
        // source: https://stackoverflow.com/a/266846
        public static String formatDuration(Duration duration)
        {
                long seconds = duration.getSeconds();
                long absSeconds = Math.abs(seconds);
                String positive = String.format(
                        "%d:%02d:%02d",
                        absSeconds / 3600,
                        (absSeconds % 3600) / 60,
                        absSeconds % 60
                );
                return (seconds < 0 ? "-" + positive : positive);
        }

        Contest showContestSetupDialog()
        {
                Contest newContest = null;

                JPanel panel = new JPanel();
                
                JLabel problemsLabel = new JLabel("Total Problems (A - ?)");
                JComboBox problemsComboBox = new JComboBox("ABCDEFGHIJKLMOPQRSTUVWXYZ".split(""));
                problemsComboBox.setSelectedIndex(4);
                problemsComboBox.setEditable(false);
                
                JLabel durationLabel = new JLabel("Duration");
                JPanel durationPanel = new JPanel();
                
                /* durationPanel begins */
                JLabel hoursLabel = new JLabel("Hours");
                Integer[] hoursComboBoxOptions = new Integer[13];
                for (int i = 0; i <= 12; i++)
                {
                        hoursComboBoxOptions[i] = i;
                }
                JComboBox hoursComboBox = new JComboBox(hoursComboBoxOptions);
                hoursComboBox.setSelectedIndex(3);
                hoursComboBox.setEditable(false);
                
                JLabel minutesLabel = new JLabel("Minutes");
                Integer[] minutesComboBoxOptions = new Integer[12];
                for (int i = 0; 5 * i < 60; i++)
                {
                        minutesComboBoxOptions[i] = 5 * i;
                }
                JComboBox minutesComboBox = new JComboBox(minutesComboBoxOptions);
                minutesComboBox.setEditable(false);
                
                durationPanel.add(hoursLabel);
                durationPanel.add(hoursComboBox);
                durationPanel.add(minutesLabel);
                durationPanel.add(minutesComboBox);
                /* durationPanel ends */
                
                JLabel penaltyLabel = new JLabel("Penalty (in minutes)");
                Integer[] penaltyComboBoxOptions = new Integer[12];
                for (int i = 0; 5 * i < 60; i++)
                {
                        penaltyComboBoxOptions[i] = 5 * i;
                }
                JComboBox penaltyComboBox = new JComboBox(penaltyComboBoxOptions);
                penaltyComboBox.setSelectedIndex(4);
                penaltyComboBox.setEditable(false);
                
                JLabel teamNameLabel = new JLabel("Team Name");
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
                
                JLabel InstitutionLabel = new JLabel("Institution Name");
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
                                if (institutionTextField.getText().equals(""))
                                {
                                        institutionTextField.setText("MyInstitution");
                                }
                        }
                });
                
                panel.setLayout(new GridLayout(5, 2));
                panel.add(problemsLabel);
                panel.add(problemsComboBox);
                panel.add(durationLabel);
                panel.add(durationPanel);
                panel.add(penaltyLabel);
                panel.add(penaltyComboBox);
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
                        Duration penaltyPerNonAC = Duration.parse("PT" + penaltyComboBox.getSelectedItem() + "M");
                        Duration contestDuration = Duration.parse(
                                "PT" + hoursComboBox.getSelectedItem() + "H" + minutesComboBox.getSelectedItem() + "M"
                        );
                        
                        newContest = new Contest(numberOfProblems, teamName, institution, penaltyPerNonAC, contestDuration);
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
                
                setLayout(new BorderLayout());
                
                add(new TopPanel(), BorderLayout.NORTH);
                add(new SubmissionPanel(), BorderLayout.CENTER);
                add(new BottomPanel(), BorderLayout.SOUTH);

                setSize(960, 540);
                setResizable(false);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setVisible(true);
        }
        
        private class ProblemGUIEntity extends JPanel
        {
                final Problem problem;
                JLabel symbolLabel, scoreLabel, timeSolvedLabel;
                JPanel solvedPanel;
                
                ProblemGUIEntity(Problem problem)
                {                                                
                        this.problem = problem;
                        
                        symbolLabel = new JLabel(problem.problemSymbol);
                        symbolLabel.setHorizontalAlignment(JLabel.CENTER);
                        changeFont(symbolLabel, symbolLabel.getFont().deriveFont(Font.BOLD, 20));
                        
                        scoreLabel = new JLabel("-");
                        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
                        
                        timeSolvedLabel = new JLabel("-");
                        timeSolvedLabel.setHorizontalAlignment(JLabel.CENTER);
                        
                        setLayout(new GridLayout(3, 1));
                        setBorder(new EmptyBorder(10, 5, 10, 5));
                        
                        add(symbolLabel);
                        add(scoreLabel);
                        add(timeSolvedLabel);
                }
                
                public void update()
                {
                        if (problem.isSolved)
                        {
                                setBackground(Color.green);
                                
                                scoreLabel.setText("1 (" + (problem.numberOfAttempts - 1) +")");
                                timeSolvedLabel.setText(
                                        formatDuration(Duration.between(contest.startTime, problem.solveTime))
                                );
                        }
                        else if (problem.numberOfAttempts > 0)
                        {
                                setBackground(Color.red);
                                
                                scoreLabel.setText("0 (" + problem.numberOfAttempts +")");
                        }
                }
        }
        
        private class ScorecardPanel extends JPanel
        {
                JPanel teamDetailsPanel, scorePanel;
                JLabel teamNameLabel, institutionLabel, scoreLabel, timePenaltyLabel;
                ProblemGUIEntity[] problemList;
                
                ScorecardPanel()
                {
                        setLayout(new GridLayout(1, 2 + contest.numberOfProblems));
                        
                        teamDetailsPanel = new JPanel();
                        teamDetailsPanel.setLayout(new GridLayout(2, 1));
                        /* teamDetailsPanel begins */
                        teamNameLabel = new JLabel(contest.teamName);
                        teamNameLabel.setHorizontalAlignment(JLabel.CENTER);
                        changeFont(teamNameLabel, teamNameLabel.getFont().deriveFont(Font.BOLD, 15));
                        teamDetailsPanel.add(teamNameLabel);

                        
                        institutionLabel = new JLabel(contest.institution);
                        institutionLabel.setHorizontalAlignment(JLabel.CENTER);
                        changeFont(institutionLabel, institutionLabel.getFont().deriveFont(Font.ITALIC));
                        teamDetailsPanel.add(institutionLabel);
                        /* teamDetailsPanel ends */
                        teamDetailsPanel.setBorder(new EmptyBorder(10, 10, 10, 5));
                        add(teamDetailsPanel);
                        
                        scorePanel = new JPanel();
                        scorePanel.setLayout(new GridLayout(2, 1));
                        /* scorePanel begins */
                        scoreLabel = new JLabel(Integer.toString(contest.score));
                        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
                        scorePanel.add(scoreLabel);
                        
                        timePenaltyLabel = new JLabel(formatDuration(contest.timePenalty));
                        timePenaltyLabel.setHorizontalAlignment(JLabel.CENTER);
                        scorePanel.add(timePenaltyLabel);
                        /* scorePanel ends */
                        scorePanel.setBorder(new EmptyBorder(10, 5, 10, 5));
                        add(scorePanel);
                        
                        problemList = new ProblemGUIEntity[contest.numberOfProblems];
                        for (int i = 0; i < contest.numberOfProblems; i++)
                        {
                                problemList[i] = new ProblemGUIEntity(contest.problems[i]);
                                add(problemList[i]);
                        }
                }
        }
        
        private class TimerPanel extends JPanel
        {
                
        }
        
        private class TopPanel extends JPanel
        {
                JScrollPane scorecardPanelScrollPane;
                
                TopPanel()
                {
                        setLayout(new BorderLayout());
                        setBorder(new EmptyBorder(10, 10, 5, 5));
                        
                        scorecardPanelScrollPane = new JScrollPane(new ScorecardPanel());
                        scorecardPanelScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);  
                        scorecardPanelScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                        scorecardPanelScrollPane.setPreferredSize(new Dimension(640, 120));
                        scorecardPanelScrollPane.setBorder(new EtchedBorder());
                        add(scorecardPanelScrollPane, BorderLayout.WEST);
                        
                        add(new TimerPanel(), BorderLayout.EAST);
                }
        }
        
        private class SubmissionPanel extends JPanel
        {
                
        }
        
        private class InputPanel extends JPanel
        {
                
        }
        
        private class ButtonPanel extends JPanel
        {
                
        }
        
        private class BottomPanel extends JPanel
        {
                
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
