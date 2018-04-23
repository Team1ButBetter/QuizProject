
// Quiz GUI

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Random;
import javafx.application.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class QuizGui extends Application {
	public static String LangChoice = new String();
	ArrayList<Integer> numbers = new ArrayList<Integer>();
	ArrayList<Integer> playerClick = new ArrayList<Integer>();
	ArrayList<Integer> playerChoice = new ArrayList<Integer>();
	int j = 0;
	int random;
	Button buttonRestart = new Button("Restart Quiz?");


	public class QuestionData {
		private SimpleStringProperty quest, answer1, answer2, answer3, answer4, image1, image2, image3, image4;

		public String getQuestFind() {
			return quest.get();
		}

		public String getAnswer1Find() {
			return answer1.get();
		}

		public String getAnswer2Find() {
			return answer2.get();
		}

		public String getAnswer3Find() {
			return answer3.get();
		}

		public String getAnswer4Find() {
			return answer4.get();
		}

		public String getImage1Find() {
			return image1.get();
		}

		public String getImage2Find() {
			return image2.get();
		}

		public String getImage3Find() {
			return image3.get();
		}

		public String getImage4Find() {
			return image4.get();
		}

		private SimpleIntegerProperty answerCorrect;

		public int getanswerCorrectFind() {
			return answerCorrect.get();
		}

		QuestionData(String questFind, String answer1Find, String answer2Find, String answer3Find, String answer4Find,
				int answerCorrect, String image1Find, String image2Find, String image3Find, String image4Find) {

			this.quest = new SimpleStringProperty(questFind);

			this.answer1 = new SimpleStringProperty(answer1Find);
			this.answer2 = new SimpleStringProperty(answer2Find);
			this.answer3 = new SimpleStringProperty(answer3Find);
			this.answer4 = new SimpleStringProperty(answer4Find);
			this.answerCorrect = new SimpleIntegerProperty(answerCorrect);

			this.image1 = new SimpleStringProperty(image1Find);
			this.image2 = new SimpleStringProperty(image2Find);
			this.image3 = new SimpleStringProperty(image3Find);
			this.image4 = new SimpleStringProperty(image4Find);

		}
	}

	private final ObservableList<QuestionData> questionList = FXCollections.observableArrayList();

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
		
		buttonRestart.setOnAction( __ ->
		{
		    quizStage.close();
		    root.getChildren().clear();
		    quizStage.setScene( new Scene( new BorderPane( border ) ) );
		    chooseLang(root);
		    quizStage.show();

		} );

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
				root.getChildren().remove(LangRoot);
				giveCSV(root);
			}
		});
		buttonENG.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				QuizGui.LangChoice = "ENG";
				root.getChildren().remove(LangRoot);
				giveCSV(root);
			}
		});
	}

	// -------------------------------------------------------------------------------------------------------------------
	// Start Quiz
	// -------------------------------------------------------------------------------------------------------------------

	public void quizStart(Pane root) {

		random();

		VBox QuizRoot = new VBox();
		HBox top = new HBox();
		HBox mid = new HBox();
		HBox bot = new HBox();

		QuizRoot.getChildren().addAll(top, mid, bot);

		Label question = new Label();

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
		mid.getChildren().addAll(question, buttonRestart);
		mid.prefWidthProperty().bind(root.widthProperty());
		mid.prefHeightProperty().bind(root.heightProperty());
		mid.setAlignment(Pos.CENTER);
		bot.getChildren().addAll(C, D);

		A.setUserData(1);
		B.setUserData(2);
		C.setUserData(3);
		D.setUserData(4);

		root.getChildren().add(QuizRoot);
		nextQuestion(question, A, B, C, D, root, QuizRoot);

		A.setOnAction((ActionEvent e) -> {
			Integer A1 = (Integer) A.getUserData();
			if (A1 == questionList.get(numbers.get(j)).getanswerCorrectFind()) {

				File imageAlert = new File("./img/tick.png");
				String stringAlert = new String("Correct");
				playerClick.add(1);
				playerChoice.add(1);
				alertUS(imageAlert, stringAlert);

				j++;
				nextQuestion(question, A, B, C, D, root, QuizRoot);
			} else {
				File imageAlert = new File("./img/cross.png");
				String stringAlert = new String("Incorrect");
				alertUS(imageAlert, stringAlert);
				playerClick.add(1);
				playerChoice.add(0);
				j++;
				nextQuestion(question, A, B, C, D, root, QuizRoot);
			}
		});
		B.setOnAction((ActionEvent e) -> {
			Integer B1 = (Integer) B.getUserData();
			if (B1 == questionList.get(numbers.get(j)).getanswerCorrectFind()) {
				File imageAlert = new File("./img/tick.png");
				String stringAlert = new String("Correct");
				alertUS(imageAlert, stringAlert);
				playerClick.add(2);
				playerChoice.add(1);
				j++;
				nextQuestion(question, A, B, C, D, root, QuizRoot);
			} else {
				File imageAlert = new File("./img/cross.png");
				String stringAlert = new String("Incorrect");
				alertUS(imageAlert, stringAlert);
				playerClick.add(2);
				playerChoice.add(0);
				j++;
				nextQuestion(question, A, B, C, D, root, QuizRoot);
			}
		});
		C.setOnAction((ActionEvent e) -> {
			Integer C1 = (Integer) C.getUserData();
			if (C1 == questionList.get(numbers.get(j)).getanswerCorrectFind()) {
				File imageAlert = new File("./img/tick.png");
				String stringAlert = new String("Correct");
				alertUS(imageAlert, stringAlert);
				playerClick.add(3);
				playerChoice.add(1);
				j++;
				nextQuestion(question, A, B, C, D, root, QuizRoot);
			} else {
				File imageAlert = new File("./img/cross.png");
				String stringAlert = new String("Incorrect");
				alertUS(imageAlert, stringAlert);
				playerClick.add(3);
				playerChoice.add(0);
				j++;
				nextQuestion(question, A, B, C, D, root, QuizRoot);
			}
		});
		D.setOnAction((ActionEvent e) -> {
			Integer D1 = (Integer) D.getUserData();
			if (D1 == questionList.get(numbers.get(j)).getanswerCorrectFind()) {
				File imageAlert = new File("./img/tick.png");
				String stringAlert = new String("Correct");
				alertUS(imageAlert, stringAlert);
				playerClick.add(4);
				playerChoice.add(1);
				j++;
				nextQuestion(question, A, B, C, D, root, QuizRoot);
			} else {
				File imageAlert = new File("./img/cross.png");
				String stringAlert = new String("Incorrect");
				alertUS(imageAlert, stringAlert);
				playerClick.add(4);
				playerChoice.add(0);
				j++;
				nextQuestion(question, A, B, C, D, root, QuizRoot);
			}
		});
	}

	// -------------------------------------------------------------------------------------------------------------------
	// Next Question
	// Construct question
	// -------------------------------------------------------------------------------------------------------------------

	public void nextQuestion(Labeled question, Labeled A, Labeled B, Labeled C, Labeled D, Pane root, Object QuizRoot) {

		if (j < questionList.size()) {
			question.setText(questionList.get(numbers.get(j)).getQuestFind());

			A.setText(questionList.get(numbers.get(j)).getAnswer1Find());
			File imageFileA = new File(questionList.get(numbers.get(j)).getImage1Find());
			Image imageA = new Image(imageFileA.toURI().toString());
			A.setGraphic(new ImageView(imageA));

			B.setText(questionList.get(numbers.get(j)).getAnswer2Find());
			File imageFileB = new File(questionList.get(numbers.get(j)).getImage2Find());
			Image imageB = new Image(imageFileB.toURI().toString());
			B.setGraphic(new ImageView(imageB));

			C.setText(questionList.get(numbers.get(j)).getAnswer3Find());
			File imageFileC = new File(questionList.get(numbers.get(j)).getImage3Find());
			Image imageC = new Image(imageFileC.toURI().toString());
			C.setGraphic(new ImageView(imageC));

			D.setText(questionList.get(numbers.get(j)).getAnswer4Find());
			File imageFileD = new File(questionList.get(numbers.get(j)).getImage4Find());
			Image imageD = new Image(imageFileD.toURI().toString());
			D.setGraphic(new ImageView(imageD));
		} else {
			root.getChildren().remove(QuizRoot);
			getResult(question, root);
		}
	};

	// -------------------------------------------------------------------------------------------------------------------
	// Get Return Results
	// -------------------------------------------------------------------------------------------------------------------

	private void getResult(Labeled question, Pane root) {

		VBox ResultRoot = new VBox();
		Label title = new Label("Results:");
		Label lbs[];
		lbs = new Label[questionList.size()];
		String Additive = new String();

		for (int i = 0; i < questionList.size(); i++) {
			if (playerClick.get(i) == 1) {
				Additive = questionList.get(numbers.get(i)).getAnswer1Find();

			}
			;
			if (playerClick.get(i) == 2) {
				Additive = questionList.get(numbers.get(i)).getAnswer2Find();
			}
			;
			if (playerClick.get(i) == 3) {
				Additive = questionList.get(numbers.get(i)).getAnswer3Find();
			}
			;
			if (playerClick.get(i) == 4) {
				Additive = questionList.get(numbers.get(i)).getAnswer4Find();
			}
			;
			Image arrow = null;
			if (playerChoice.get(i) == 1) {
				File fill = new File("./img/tick.png");
				arrow = new Image(fill.toURI().toString(),60, 60, false, false);

			}
			;

			if (playerChoice.get(i) == 0) {
				File fill = new File("./img/cross.png");
				arrow = new Image(fill.toURI().toString(),60, 60, false, false);
			}
			;


			String String = questionList.get(numbers.get(i)).getQuestFind() + "     " + Additive;
			;
			lbs[i] = new Label(String);
			lbs[i].setGraphic(new ImageView(arrow));
			
		}

		ResultRoot.getChildren().addAll(lbs);
		root.getChildren().addAll(buttonRestart, title, ResultRoot);
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
	// Random number generator
	// -------------------------------------------------------------------------------------------------------------------

	public ArrayList<Integer> random() {
		Random randomGenerator = new Random();
		while (numbers.size() < questionList.size()) {

			int random = randomGenerator.nextInt(questionList.size());
			if (!numbers.contains(random)) {
				numbers.add(random);

			}
		}
		return numbers;
	}

	// -------------------------------------------------------------------------------------------------------------------
	// CSV Reader - Constructor
	// -------------------------------------------------------------------------------------------------------------------

	private void giveCSV(Pane root) {

		File findCSVfile = new File("./file/dummy_" + LangChoice + ".csv");
		String FieldDelimiter = ",";
		BufferedReader CSVbuffer;

		try {
			CSVbuffer = new BufferedReader(new FileReader(findCSVfile));

			String line;
			while ((line = CSVbuffer.readLine()) != null) {
				String[] Sfields = line.split(FieldDelimiter, -1);
				String questField = (Sfields[0]);
				String answer1Field = (Sfields[1]);
				String answer2Field = (Sfields[2]);
				String answer3Field = (Sfields[3]);
				String answer4Field = (Sfields[4]);
				int answerCorrect = Integer.parseInt(Sfields[5]);
				String image1 = (Sfields[6]);
				String image2 = (Sfields[7]);
				String image3 = (Sfields[8]);
				String image4 = (Sfields[9]);

				QuestionData questionrecord = new QuestionData(questField, answer1Field, answer2Field, answer3Field,
						answer4Field, answerCorrect, image1, image2, image3, image4);
				questionList.add(questionrecord);
			}
			quizStart(root);
			// exceptions
		} catch (FileNotFoundException ex) {
			Logger.getLogger(QuizGui.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(QuizGui.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	// -------------------------------------------------------------------------------------------------------------------
	// Launch the Program / Main
	// -------------------------------------------------------------------------------------------------------------------
	public static void main(String[] args) {
		launch(args);
	}
}