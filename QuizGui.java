
// Quiz GUI

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class QuizGui extends Application {
	public static String LangChoice = new String();
	int j = 0;
	int score = 0;
	ArrayList<Integer> playerClick = new ArrayList<Integer>();
	ArrayList<Integer> playerChoice = new ArrayList<Integer>();
	boolean completed = false;
	String filePath = "test.csv";
	String quizPath = "./file/dummy_";
	Button buttonRestart = new Button("Restart Quiz?");
	

	public static class MathsHelper {
		static <T> ArrayList<T> shuffle(ArrayList<T> list) {
			ArrayList<T> output = new ArrayList<T>();
			boolean[] isSelected = new boolean[list.size()];
			for (boolean bool : isSelected) {
				bool = false;
			}

			for (int i = 0; i < list.size(); i++) {
				int nextIndex = (int) (Math.random() * list.size());

				if (!isSelected[nextIndex]) {
					isSelected[nextIndex] = true;
					output.add(list.get(nextIndex));
				} else {
					i--;
				}
			}
			return output;
		}

		public static double mean(double[] nums) {
			double sum = 0;
			for (double num : nums) {
				sum += num;
			}
			return sum / nums.length;
		}

		public static double median(double[] nums) {
			int[] ranked = new int[nums.length];
			boolean[] available = new boolean[nums.length];
			for (boolean b : available) {
				b = true;
			}
			for (int i = 0; i < ranked.length; i++) {
				int index = 0;
				double smallest = nums[index];
				for (int j = 0; j < nums.length; j++) {
					if (available[j] && nums[j] < smallest) {
						smallest = nums[j];
						index = j;
					}
				}
				ranked[i] = index;
				available[index] = false;
			}
			return ranked[(int) Math.floor((nums.length + 1) / 2)];
		}

		public static double standardDeviation(double[] nums) {
			double mean = mean(nums);
			double var = 0;
			for (int i = 0; i < nums.length; i++) {
				var += Math.pow(nums[i] - mean, 2);
			}
			return Math.sqrt(var);
		}

		public static double skew(double[] nums) {
			double mean = mean(nums);
			double median = median(nums);
			double sd = standardDeviation(nums);
			return (mean - median) / sd;
		}
	}

	public class Answer {
		private SimpleStringProperty text = new SimpleStringProperty(), image = new SimpleStringProperty();
		public boolean isCorrect, isSelected, isAnswered, isSkipped;

		public String getText() {
			return text.get();
		}

		public void setText(String arg) {
			this.text.set(arg);
		}

		public String getImage() {
			return image.get();
		}

		public void setImage(String imageString) {
			this.image.set(imageString);
		}

		Answer(String text, String image, boolean isCorrect) {
			setText(text);
			setImage(image);
			this.isCorrect = isCorrect;
			isSelected = false;
			isAnswered = false;
			isSkipped = false;
		}
	}

	public class QuestionData {
		private SimpleStringProperty quest = new SimpleStringProperty();
		public Answer[] answers = new Answer[4];
		public int correctId = 0;
		int questionNumber;
		boolean reset = false;
		boolean attempted = false;

		QuestionData(Answer[] answers, String questionText, int correctId, int qId) {
			this.answers = answers;
			this.quest.set(questionText);
			this.correctId = correctId;
			this.questionNumber = qId;
		}

		public String getQuestFind() {
			return quest.get();
		}

		public Answer getAnswer(int id) {
			return answers[id];
		}

		public Answer[] getAnswers() {
			return answers;
		}

		Answer correctAnswer() {
			return answers[correctId];
		}

		int getCorrectId() {
			return correctId;
		}
	}

	private final ObservableList<QuestionData> questionList = FXCollections.observableArrayList();
	int[] questionOrder = new int[questionList.size()];
	int questions = questionList.size();

	public void start(Stage quizStage) {
		quizStage.setTitle("Quiz App");

		BorderPane border = new BorderPane();
		border.getStylesheets().add("style.css");
		HBox deepRoot = new HBox();
		VBox root = new VBox();
		deepRoot.getChildren().add(root);
		border.setCenter(deepRoot);
		quizStage.setScene(new Scene(border));

		root.setAlignment(Pos.CENTER);
		deepRoot.setAlignment(Pos.CENTER);

		quizStage.setMaximized(true);
		border.prefWidthProperty().bind(quizStage.widthProperty());
		border.prefHeightProperty().bind(quizStage.heightProperty());
		deepRoot.prefWidthProperty().bind(border.widthProperty());
		deepRoot.prefHeightProperty().bind(border.heightProperty());

		quizStage.show();
		chooseLang(root);

		buttonRestart.setOnAction(__ -> {
			if (j-1 < questionList.size()) {
			questionList.get(j - 1).reset = true;
			}else
			{}
			
			try {
				
				updateStats(filePath, questionList);
			} catch (IOException e) {
				e.printStackTrace();
			}
			quizPath = "./file/dummy_";
			LangChoice = new String();
			j = 0;
			score = 0;
			playerClick = new ArrayList<Integer>();
			playerChoice = new ArrayList<Integer>();
			completed = false;
			quizStage.close();
			root.getChildren().clear();
			quizStage.setScene(new Scene(new BorderPane(border)));
			chooseLang(root);
			quizStage.show();

		});
	};

	// -------------------------------------------------------------------------------------------------------------------
	// Choose Language
	// -------------------------------------------------------------------------------------------------------------------

	public void chooseLang(Pane root) {
		HBox LangRoot = new HBox();

		File imageFile = new File("./img/enImg.png");
		Image imageEng = new Image(imageFile.toURI().toString());
		Button buttonENG = new Button("", new ImageView(imageEng));
		buttonENG.prefWidthProperty().bind(root.widthProperty());
		buttonENG.prefHeightProperty().bind(root.heightProperty());

		File imageFileC = new File("./img/cyImg.png");
		Image imageCym = new Image(imageFileC.toURI().toString());
		Button buttonCYM = new Button("", new ImageView(imageCym));
		buttonCYM.prefWidthProperty().bind(root.widthProperty());
		buttonCYM.prefHeightProperty().bind(root.heightProperty());

		LangRoot.getChildren().addAll(buttonENG, buttonCYM);
		root.getChildren().add(LangRoot);

		String LangChoice = new String();
		if (LangChoice != null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
			}
		}
		buttonCYM.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				QuizGui.LangChoice = "CYN";
				quizPath += "CYN.csv";
				root.getChildren().remove(LangRoot);
				giveCSV(root, quizPath);
			}
		});
		buttonENG.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				QuizGui.LangChoice = "ENG";
				quizPath += "ENG.csv";
				System.out.println(quizPath);
				root.getChildren().remove(LangRoot);
				giveCSV(root, quizPath);
			}
		});
	}

	// -------------------------------------------------------------------------------------------------------------------
	// Start Quiz
	// -------------------------------------------------------------------------------------------------------------------

	public void quizStart(Pane root) {

		VBox QuizRoot = new VBox();
		HBox top = new HBox();
		HBox mid = new HBox();
		HBox bot = new HBox();

		QuizRoot.getChildren().addAll(top, mid, bot);

		Label question = new Label();
		Label qnum = new Label();
		qnum.setText(("" + (j+1) + "/" + (questionList.size()+1)+ "    "));

		Button A = new Button("");
		A.prefWidthProperty().bind(root.widthProperty());
		A.prefHeightProperty().bind(root.heightProperty());
		Button B = new Button("");
		B.prefWidthProperty().bind(root.widthProperty());
		B.prefHeightProperty().bind(root.heightProperty());
		Button C = new Button("");
		C.prefWidthProperty().bind(root.widthProperty());
		C.prefHeightProperty().bind(root.heightProperty());
		Button D = new Button("");
		D.prefWidthProperty().bind(root.widthProperty());
		D.prefHeightProperty().bind(root.heightProperty());
		top.getChildren().addAll(A, B);
		mid.getChildren().addAll(qnum, question, buttonRestart);
		mid.prefWidthProperty().bind(root.widthProperty());
		mid.prefHeightProperty().bind(root.heightProperty());
		mid.setAlignment(Pos.CENTER);
		bot.getChildren().addAll(C, D);

		A.setUserData(1);
		B.setUserData(2);
		C.setUserData(3);
		D.setUserData(4);

		root.getChildren().add(QuizRoot);
		Labeled[] buttons = { A, B, C, D };
		nextQuestion(question, buttons, root, QuizRoot);

		A.setOnAction((ActionEvent e) -> {

			questionList.get(j - 1).attempted = true;
			questionList.get(j - 1).answers[0].isSelected = true;
			Integer A1 = (Integer) A.getUserData();
			qnum.setText(("" + (j+1) + "/" + (questionList.size()+1)+ "    "));
			if (A1 == questionList.get(j - 1).getCorrectId()) {

				File imageAlert = new File("./img/tick.png");
				String stringAlert = new String("Correct");
				score++;
				playerClick.add(1);
				playerChoice.add(1);
				alertUS(imageAlert, stringAlert);
				nextQuestion(question, buttons, root, QuizRoot);
			} else {
				File imageAlert = new File("./img/cross.png");
				String stringAlert = new String("Incorrect");
				alertUS(imageAlert, stringAlert);
				playerClick.add(1);
				playerChoice.add(0);
				nextQuestion(question, buttons, root, QuizRoot);
			}
		});
		B.setOnAction((ActionEvent e) -> {
			questionList.get(j - 1).attempted = true;
			questionList.get(j - 1).answers[1].isSelected = true;
			Integer B1 = (Integer) B.getUserData();
			qnum.setText(("" + (j+1) + "/" + (questionList.size()+1)+ "    "));
			if (B1 == questionList.get(j - 1).getCorrectId()) {
				File imageAlert = new File("./img/tick.png");
				score++;
				playerClick.add(2);
				playerChoice.add(1);
				String stringAlert = new String("Correct");
				alertUS(imageAlert, stringAlert);
				nextQuestion(question, buttons, root, QuizRoot);
			} else {
				File imageAlert = new File("./img/cross.png");
				String stringAlert = new String("Incorrect");
				alertUS(imageAlert, stringAlert);
				playerClick.add(2);
				playerChoice.add(0);
				nextQuestion(question, buttons, root, QuizRoot);
			}
		});
		C.setOnAction((ActionEvent e) -> {
			questionList.get(j - 1).attempted = true;
			questionList.get(j - 1).answers[2].isSelected = true;
			Integer C1 = (Integer) C.getUserData();
			qnum.setText(("" + (j+1) + "/" + (questionList.size()+1)+ "    "));
			if (C1 == questionList.get(j - 1).getCorrectId()) {
				File imageAlert = new File("./img/tick.png");
				score++;
				playerClick.add(3);
				playerChoice.add(1);
				String stringAlert = new String("Correct");
				alertUS(imageAlert, stringAlert);
				nextQuestion(question, buttons, root, QuizRoot);
			} else {
				File imageAlert = new File("./img/cross.png");
				String stringAlert = new String("Incorrect");
				alertUS(imageAlert, stringAlert);
				playerClick.add(3);
				playerChoice.add(0);
				nextQuestion(question, buttons, root, QuizRoot);
			}
		});
		D.setOnAction((ActionEvent e) -> {
			questionList.get(j - 1).attempted = true;
			questionList.get(j - 1).answers[3].isSelected = true;
			Integer D1 = (Integer) D.getUserData();
			qnum.setText(("" + (j+1) + "/" + (questionList.size()+1)+ "    "));
			if (D1 == questionList.get(j - 1).getCorrectId()) {
				File imageAlert = new File("./img/tick.png");
				String stringAlert = new String("Correct");
				alertUS(imageAlert, stringAlert);
				score++;
				playerClick.add(4);
				playerChoice.add(1);
				nextQuestion(question, buttons, root, QuizRoot);
			} else {
				File imageAlert = new File("./img/cross.png");
				String stringAlert = new String("Incorrect");
				alertUS(imageAlert, stringAlert);
				playerClick.add(4);
				playerChoice.add(0);
				nextQuestion(question, buttons, root, QuizRoot);
			}
		});
	}

	// -------------------------------------------------------------------------------------------------------------------
	// Next Question
	// Construct question
	// -------------------------------------------------------------------------------------------------------------------

	public void nextQuestion(Labeled question, Labeled[] buttons, Pane root, Object QuizRoot) {

		ArrayList<SimpleIntegerProperty> orderObj = new ArrayList<SimpleIntegerProperty>();
		for (int i = 0; i < 4; i++) {
			orderObj.add(new SimpleIntegerProperty(i));
		}
		// TODO: if answers are to be shuffled, how the text and images are displayed on
		// the buttons needs to be similarly updated
		// orderObj = MathsHelper.shuffle(orderObj);
		int[] order = new int[orderObj.size()];
		for (int i = 0; i < orderObj.size(); i++) {
			order[i] = orderObj.get(i).get();
		}
		if (j < questionList.size() && questionList.get(j) != null) {
			question.setText(questionList.get(j).getQuestFind());
			for (int i = 0; i < buttons.length; i++) {
				buttons[i].setText(questionList.get(j).getAnswer(order[i]).getText());
				File imageFile = new File(questionList.get(j).getAnswer(order[i]).getImage());
				Image image = new Image(imageFile.toURI().toString());
				buttons[order[i]].setGraphic(new ImageView(image));
			}
		} else {
			root.getChildren().remove(QuizRoot);
			getResult(question, root);
			try {
				updateStats(filePath, questionList);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		j++;
	};

	// -------------------------------------------------------------------------------------------------------------------
	// Get Results
	// -------------------------------------------------------------------------------------------------------------------

	private void getResult(Labeled question, Pane root) {

		VBox ResultRoot = new VBox();
		Label title = new Label("Results:");
		Label lbs[];
		lbs = new Label[questionList.size()];
		String Additive = new String();

		for (int i = 0; i < questionList.size(); i++) {
			if (playerClick.get(i) == 1) {
				Additive = questionList.get(i).answers[0].getText();
			}
			;
			if (playerClick.get(i) == 2) {
				Additive = questionList.get(i).answers[1].getText();
			}
			;
			if (playerClick.get(i) == 3) {
				Additive = questionList.get(i).answers[2].getText();
			}
			;
			if (playerClick.get(i) == 4) {
				Additive = questionList.get(i).answers[3].getText();
			}
			;
			Image arrow = null;
			if (playerChoice.get(i) == 1) {
				File fill = new File("./img/tick.png");
				arrow = new Image(fill.toURI().toString(), 60, 60, false, false);

			}
			;

			if (playerChoice.get(i) == 0) {
				File fill = new File("./img/cross.png");
				arrow = new Image(fill.toURI().toString(), 60, 60, false, false);
			}
			;

			String String = questionList.get(i).getQuestFind() + "     " + Additive;
			;
			lbs[i] = new Label(String);
			lbs[i].setGraphic(new ImageView(arrow));

		}

		ResultRoot.getChildren().addAll(lbs);
		root.getChildren().addAll(buttonRestart, title, ResultRoot);

		System.out.println("Done");
		completed = true;
	}

	// -------------------------------------------------------------------------------------------------------------------
	// Alert Code - Constructor
	// -------------------------------------------------------------------------------------------------------------------

	private void alertUS(File imageAlert, String stringAlert) {
		Alert alert = new Alert(AlertType.NONE);
		alert.initStyle(StageStyle.UTILITY);
		alert.setHeaderText(stringAlert);
		alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
		Image imagethe = new Image(imageAlert.toURI().toString());
		ImageView imageView = new ImageView(imagethe);
		imageView.setFitWidth(100);
		imageView.setFitHeight(100);
		alert.setGraphic(imageView);

		Thread thread = new Thread(() -> {
			try {
				Thread.sleep(1000);
				if (alert.isShowing()) {
					Platform.runLater(() -> alert.close());
				}
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		});
		thread.setDaemon(true);
		thread.start();
		Optional<ButtonType> result = alert.showAndWait();
	}

	// -------------------------------------------------------------------------------------------------------------------
	// CSV Reader - Constructor
	// -------------------------------------------------------------------------------------------------------------------

	private void giveCSV(Pane root, String filePath) {

		File findCSVfile = new File(filePath);
		String FieldDelimiter = ",";
		BufferedReader CSVbuffer;

		try {
			CSVbuffer = new BufferedReader(new FileReader(findCSVfile));
			ArrayList<QuestionData> questionArr = new ArrayList<QuestionData>();
			String line;
			int k = 0;
			while ((line = CSVbuffer.readLine()) != null) {
				String[] Sfields = line.split(FieldDelimiter, -1);
				String questField = (Sfields[0]);
				Answer[] answers = new Answer[4];
				for (int i = 1; i < 5; i++) {
					answers[i - 1] = new Answer(Sfields[i], Sfields[i + 5], i == Integer.parseInt(Sfields[5]));
				}
				int answerCorrect = Integer.parseInt(Sfields[5]);

				QuestionData questionrecord = new QuestionData(answers, questField, answerCorrect, k);
				k++;
				questionArr.add(questionrecord);
			}

			questionArr = MathsHelper.shuffle(questionArr);
			questionList.setAll(questionArr);

			quizStart(root);
			// exceptions
		} catch (FileNotFoundException ex) {
			Logger.getLogger(QuizGui.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(QuizGui.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	// Acquire statistical data
	// -------------------------------------------------------------------------------------------------------------------
	public void updateStats(String filePath, ObservableList<QuestionData> questions)
			throws IOException, FileNotFoundException {
		File statsFile = new File(filePath);
		if (!statsFile.exists()) {
			FileWriter writer = new FileWriter(statsFile, true);
			try {
				statsFile.createNewFile();
				String s = "0,0,A,A,A" + System.getProperty("line.separator");
				for (int i = 0; i < questionList.size(); i++) {
					s += System.getProperty("line.separator") + Integer.toString(i);
					for (int k = 0; k < 8; k++) {
						s += ",0";
					}
				}
				writer.write(s);
			} catch (IOException ex) {
				System.out.println("save failed");
			}
			writer.close();
		}
		BufferedReader nextLine = new BufferedReader(new FileReader(statsFile));
		// user data
		String userData = nextLine.readLine();
		// quiz data
		String[] dataValues;
		int[] quizStats = {};
		String valuesString = "";
		if ((valuesString = nextLine.readLine()) != null) {
			dataValues = valuesString.split(",");
			if (completed) {
				quizStats = new int[dataValues.length + 1];
				for (int i = 0; i < dataValues.length; i++) {
					quizStats[i] = Integer.parseInt(dataValues[i]);
				}
				quizStats[1]++;
				quizStats[quizStats.length - 1] = score;
			} else {
				quizStats = new int[dataValues.length];
				for (int i = 0; i < dataValues.length; i++) {
					quizStats[i] = Integer.parseInt(dataValues[i]);
				}
			}
			quizStats[0]++;
		}

		// question data
		ArrayList<int[]> questionStats = new ArrayList<int[]>();
		while ((valuesString = nextLine.readLine()) != null) {
			dataValues = valuesString.split(",");
			int[] nextArr = new int[dataValues.length];
			for (int i = 0; i < nextArr.length; i++) {
				nextArr[i] = Integer.parseInt(dataValues[i]);
			}
			questionStats.add(nextArr);
		}
		nextLine.close();

		String writeString = userData + System.getProperty("line.separator");
		for (QuestionData question : questions) {
			for (int i = 0; i < question.answers.length; i++) {
				if (question.answers[i].isSelected) {
					questionStats.get(question.questionNumber)[1 + i]++;
					if (question.answers[i].isCorrect) {
						questionStats.get(question.questionNumber)[5]++;
					}
				}
			}
			if (question.reset) {
				questionStats.get(question.questionNumber)[6]++;
			}
			if (question.attempted) {
				questionStats.get(question.questionNumber)[7]++;
			}
		}

		// write values back into file

		// write user data
		FileWriter writer = new FileWriter(statsFile, false); // delete contents
		writer.close();
		writer = new FileWriter(statsFile, true);
		writer.write(writeString);
		writeString = "";
		// write quiz data
		for (int i : quizStats) {
			writeString += i + ",";
		}
		writeString += System.getProperty("line.separator");
		// write question data
		writer.write(writeString);
		for (int[] record : questionStats) {
			writeString = "";
			for (int i = 0; i < record.length; i++) {
				writeString += Integer.toString(record[i]) + ",";
			}
			writeString += System.getProperty("line.separator");
			writer.write(writeString);
		}
		writer.close();
	}

	// -------------------------------------------------------------------------------------------------------------------
	// Launch the Program / Main
	// -------------------------------------------------------------------------------------------------------------------
	public static void main(String[] args) {
		launch(args);
	}
}