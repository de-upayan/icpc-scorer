/*
        author: Upayan De (de-upayan)
*/

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
        static int lastSubmissionID = 0;
        Verdict verdict;
        Instant submissionTime;
        
        Submission(int problemID, Verdict verdict, Instant submissionTime)
        {
                this.submissionID = ++lastSubmissionID;
                this.problemID = problemID;
                this.verdict = verdict;
                this.submissionTime = submissionTime;
        }
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
                //this.contestDuration = Duration.parse("PT20S"); // For testing the timer
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
        TopPanel topPanel;
        MidPanel midPanel;
        JScrollPane midPanelScrollPane;
        BottomPanel bottomPanel;
        
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
                problemsComboBox.setSelectedIndex(5);
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
                
                add(topPanel = new TopPanel(), BorderLayout.NORTH);
                
                midPanelScrollPane = new JScrollPane(midPanel = new MidPanel());
                midPanelScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);  
                midPanelScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                midPanelScrollPane.setBorder(new EtchedBorder());
                add(midPanelScrollPane, BorderLayout.CENTER);                
                
                add(bottomPanel = new BottomPanel(), BorderLayout.SOUTH);

                setSize(960, 540);
                setResizable(false);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setVisible(true);
        }
        
        void endContest()
        {
                contest.isContestActive = false;
                
                bottomPanel.recordButton.setEnabled(false);
                bottomPanel.endContestButton.setEnabled(false);
                topPanel.timerPanel.timerThread.stop();
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
                        changeFont(symbolLabel, symbolLabel.getFont().deriveFont(Font.BOLD, 24));
                        
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
                        changeFont(teamNameLabel, teamNameLabel.getFont().deriveFont(Font.BOLD, 18));
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
                        changeFont(scorePanel, scorePanel.getFont().deriveFont(Font.BOLD, 16));
                        add(scorePanel);
                        
                        problemList = new ProblemGUIEntity[contest.numberOfProblems];
                        for (int i = 0; i < contest.numberOfProblems; i++)
                        {
                                problemList[i] = new ProblemGUIEntity(contest.problems[i]);
                                add(problemList[i]);
                        }
                }
                
                void updateScore()
                {
                        scoreLabel.setText(Integer.toString(contest.score));
                        timePenaltyLabel.setText(formatDuration(contest.timePenalty));
                }
        }
        
        private class TimerPanel extends JPanel implements Runnable
        {
                Thread timerThread;
                JLabel timerLabel;
                Duration currentRemaining;
                
                TimerPanel()
                {                        
                        currentRemaining = contest.contestDuration;
                        
                        timerLabel = new JLabel(formatDuration(currentRemaining));
                        timerLabel.setBorder(BorderFactory.createTitledBorder("Time Remaining"));
                        timerLabel.setHorizontalAlignment(JLabel.CENTER);
                        timerLabel.setPreferredSize(new Dimension(290, 120));
                        changeFont(timerLabel, timerLabel.getFont().deriveFont(Font.BOLD, 35));
                        add(timerLabel, BorderLayout.CENTER);
                        
                        timerThread = new Thread(this);
                        timerThread.start();
                }
                
                @Override
                public void run()
                {
                        while (true)
                        {
                                currentRemaining = contest.contestDuration.minus(
                                        Duration.between(contest.startTime, Instant.now())
                                );
                                
                                if (currentRemaining.isZero() || currentRemaining.isNegative())
                                {
                                        break;
                                }
                                
                                timerLabel.setText(formatDuration(currentRemaining));
                        }
                        
                        endContest();
                }              
        }
        
        private class TopPanel extends JPanel
        {
                ScorecardPanel scorecardPanel;
                TimerPanel timerPanel;
                JScrollPane scorecardPanelScrollPane;
                
                TopPanel()
                {
                        setLayout(new BorderLayout());
                        setBorder(new EmptyBorder(10, 10, 5, 5));
                        
                        scorecardPanelScrollPane = new JScrollPane(scorecardPanel = new ScorecardPanel());
                        scorecardPanelScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);  
                        scorecardPanelScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                        scorecardPanelScrollPane.setPreferredSize(new Dimension(640, 120));
                        scorecardPanelScrollPane.setBorder(new EtchedBorder());
                        add(scorecardPanelScrollPane, BorderLayout.WEST);
                        
                        add(timerPanel = new TimerPanel(), BorderLayout.EAST);
                }
        }
        
        private class MidPanel extends JPanel
        {                
                MidPanel()
                {
                        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); 
                                        
                        JPanel headerPanel = new JPanel();
                        headerPanel.setPreferredSize(new Dimension(950, 75));
                        headerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                        headerPanel.setLayout(new GridLayout(1, 4));
                        /* headerPanel begins */
                        JLabel label;
                        
                        label = new JLabel("Submission ID");
                        label.setHorizontalAlignment(JLabel.CENTER);
                        headerPanel.add(label);
                        
                        label = new JLabel("Submission Time");
                        label.setHorizontalAlignment(JLabel.CENTER);
                        headerPanel.add(label);
                        
                        label = new JLabel("Problem");
                        label.setHorizontalAlignment(JLabel.CENTER);
                        headerPanel.add(label);
                        
                        label = new JLabel("Verdict");
                        label.setHorizontalAlignment(JLabel.CENTER);
                        headerPanel.add(label);
                        /* headerPanel ends */
                        
                        changeFont(headerPanel, headerPanel.getFont().deriveFont(Font.BOLD, 16));
                        add(headerPanel);
                }
                
                void addSubmissionRecord(Submission newSubmission)
                {
                        JPanel panel = new JPanel();
                        panel.setPreferredSize(new Dimension(950, 75));
                        panel.setLayout(new GridLayout(1, 4));
                        /* headerPanel begins */
                        JLabel label;
                        
                        label = new JLabel("" + newSubmission.submissionID);
                        label.setHorizontalAlignment(JLabel.CENTER);
                        panel.add(label);
                        
                        label = new JLabel(formatDuration(
                                Duration.between(contest.startTime, newSubmission.submissionTime)
                        ));
                        label.setHorizontalAlignment(JLabel.CENTER);
                        panel.add(label);
                        
                        label = new JLabel(Character.toString('A' + newSubmission.problemID));
                        label.setHorizontalAlignment(JLabel.CENTER);
                        panel.add(label);
                        
                        label = new JLabel(newSubmission.verdict.toString());
                        label.setHorizontalAlignment(JLabel.CENTER);
                        panel.add(label);
                        /* headerPanel ends */
                        
                        add(panel);
                }
        }
        
        private class BottomPanel extends JPanel
        {
                JLabel problemLabel, verdictLabel;
                JComboBox problemComboBox, verdictComboBox;
                JButton recordButton, endContestButton;
                
                BottomPanel()
                {
                        setLayout(new GridLayout(1, 4));
                        setPreferredSize(new Dimension(960, 50));
                        
                        JPanel problemPanel = new JPanel();
                        /* problemPanel begins */
                        problemPanel.setLayout(new GridLayout(1, 2));
                        
                        problemLabel = new JLabel("Problem");
                        problemLabel.setBorder(new EmptyBorder(10, 10, 10, 5));
                        problemLabel.setHorizontalAlignment(JLabel.CENTER);
                        problemPanel.add(problemLabel);
                        
                        problemComboBox = new JComboBox(
                                "ABCDEFGHIJKLMOPQRSTUVWXYZ".substring(0, contest.numberOfProblems).split("")
                        );
                        problemComboBox.setBorder(new EmptyBorder(10, 5, 10, 5));
                        problemComboBox.setEditable(false);
                        problemPanel.add(problemComboBox);
                        /* problemPanel ends */
                        add(problemPanel);
                        
                        JPanel verdictPanel = new JPanel();
                        /* verdictPanel begins */
                        verdictPanel.setLayout(new GridLayout(1, 2));
                        
                        verdictLabel = new JLabel("Verdict");
                        verdictLabel.setHorizontalAlignment(JLabel.CENTER);
                        verdictLabel.setBorder(new EmptyBorder(10, 5, 10, 5));
                        verdictPanel.add(verdictLabel);
                        
                        verdictComboBox = new JComboBox(new Verdict[] {
                                Verdict.AC,
                                Verdict.WA,
                                Verdict.TLE,
                                Verdict.MLE,
                                Verdict.PE
                        });
                        verdictComboBox.setBorder(new EmptyBorder(10, 5, 10, 10));
                        verdictComboBox.setEditable(false);
                        verdictPanel.add(verdictComboBox);
                        /* verdictPanel ends */
                        add(verdictPanel);
                        
                        recordButton = new JButton("Record Submission");
                        recordButton.addActionListener(new RecordButtonListener());
                        add(recordButton);
                        
                        endContestButton = new JButton("End Contest");
                        endContestButton.addActionListener(new EndContestButtonListener());
                        add(endContestButton);
                }
                
                private class RecordButtonListener implements ActionListener
                {
                        @Override
                        public void actionPerformed(ActionEvent event)
                        {
                                int problemID;
                                Verdict verdict;
                                
                                problemID = ((String) problemComboBox.getSelectedItem()).charAt(0) - 'A';
                                verdict = (Verdict) verdictComboBox.getSelectedItem();
                                
                                Submission newSubmission = new Submission(
                                        problemID,
                                        verdict,
                                        Instant.now()
                                );
                                
                                contest.submissions.add(newSubmission);
                                midPanel.addSubmissionRecord(newSubmission);
                                
                                // Update MidPanel
                                
                                if (contest.problems[problemID].isSolved)
                                {
                                        return;
                                }
                                
                                contest.problems[problemID].numberOfAttempts++;
                                
                                if (verdict == Verdict.AC)
                                {
                                        contest.problems[problemID].isSolved = true;
                                        contest.problems[problemID].solveTime = newSubmission.submissionTime;
                                        
                                        contest.score++;
                                        
                                        contest.timePenalty = contest.timePenalty.plus(
                                                Duration.between(contest.startTime, newSubmission.submissionTime)
                                        );
                                        for (int i = 0; i < contest.problems[problemID].numberOfAttempts - 1; i++)
                                        {
                                                contest.timePenalty = contest.timePenalty.plus(contest.penaltyPerNonAC);
                                        }
                                        
                                        topPanel.scorecardPanel.updateScore();
                                }
                                
                                topPanel.scorecardPanel.problemList[problemID].update();
                        }
                }
                
                private class EndContestButtonListener implements ActionListener
                {
                        @Override
                        public void actionPerformed(ActionEvent event)
                        {
                                int confirmation = JOptionPane.showConfirmDialog(
                                        null,
                                        "Do you really wish to end the contest?",
                                        "End Contest?",
                                        JOptionPane.YES_NO_OPTION
                                );
                                
                                if (confirmation == JOptionPane.YES_OPTION)
                                {
                                        endContest();
                                }
                        }
                }
        }
}

public class Main
{
        public static void main(String args[])
        {
                Font myFont = new Font("Verdana", Font.PLAIN, 15);
                UIDefaults defaultUI = UIManager.getDefaults();
                defaultUI.put("Button.font", myFont);
                defaultUI.put("Label.font", myFont);
                defaultUI.put("Panel.font", myFont);
                defaultUI.put("ComboBox.font", myFont);
                defaultUI.put("TextField.font", myFont);
                defaultUI.put("OptionPane.font", myFont);
                defaultUI.put("ScrollPane.font", myFont);
                defaultUI.put("TitledBorder.font", myFont);
                
                ContestWindow cwin = new ContestWindow();
                return;
        }
}
