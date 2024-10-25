package com.example.krestiki;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class Friend extends AppCompatActivity {


    private int[][] board = new int[3][3];
    private int currentPlayer = 1; // 1 - крестик, 2 - нолик
    private boolean gameOver = false;
    SharedPreferences sharedPreferences;

    private Button[] cells;
    private TextView statusText;
    private Button btnexit, sbros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        //кнопка выхода
        btnexit = findViewById(R.id.exitButton);
        btnexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Friend.this, MainActivity.class);
                startActivity(intent);
            }
        });
        cells = new Button[]{
                findViewById(R.id.button00),
                findViewById(R.id.button01),
                findViewById(R.id.button02),
                findViewById(R.id.button10),
                findViewById(R.id.button11),
                findViewById(R.id.button12),
                findViewById(R.id.button20),
                findViewById(R.id.button21),
                findViewById(R.id.button22)
        };
        statusText = findViewById(R.id.statusText);

        // Инициализация SharedPreferences
        sharedPreferences = getSharedPreferences("TicTacToePrefs", Context.MODE_PRIVATE);
        loadStatistics();
        // Обработчики событий для клеток
        for (int i = 0; i < cells.length; i++) {
            final int index = i;
            cells[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleCellClick(index);
                }
            });
        }

        sbros = findViewById(R.id.sbros);
        sbros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetGame();
            }
        });

    }

    private void handleCellClick(int index) {
        if (gameOver) return;

        int row = index / 3;
        int col = index % 3;

        if (board[row][col] == 0) {
            board[row][col] = currentPlayer;
            cells[index].setText(currentPlayer == 1 ? "X" : "O");
            currentPlayer = currentPlayer == 1 ? 2 : 1;
            if (checkWin()) {
                gameOver = true;
                statusText.setText("Игрок " + (currentPlayer == 1 ? "O" : "X") + " победил!");
                updateStatistics(currentPlayer == 1, false);
            } else if (checkDraw()) {
                gameOver = true;
                statusText.setText("Ничья!");
                updateStatistics(false, true);
            } else {
                statusText.setText("Ход игрока " + (currentPlayer == 1 ? "X" : "O"));
            }
        }
    }

    private boolean checkWin() {
        // Проверка строк, столбцов и диагоналей
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != 0)
                return true;
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != 0)
                return true;
        }
        if ((board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != 0) ||
                (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != 0))
            return true;
        return false;
    }

    private boolean checkDraw() {
        // Проверка на заполненность всех клеток
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) return false;
            }
        }
        return true;
    }

    private void updateStatistics(boolean isWin, boolean isDraw) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isWin) {
            editor.putInt("wins", sharedPreferences.getInt("wins", 0) + 1);
        }
        else if (isDraw) {
            editor.putInt("draws", sharedPreferences.getInt("draws", 0) + 1);
        }
        else {
            editor.putInt("losses", sharedPreferences.getInt("losses", 0) + 1);
        }
        editor.apply();
        loadStatistics();
    }

    private void loadStatistics() {
        int wins = sharedPreferences.getInt("wins", 0);
        int losses = sharedPreferences.getInt("losses", 0);
        int draws = sharedPreferences.getInt("draws", 0);
        statusText.setText("Победы: " + wins + ", Поражения: " + losses + ", Ничьи: " + draws);
    }
    private void ResetGame(){
        gameOver = false;
        currentPlayer = 1;
        statusText.setText("Ход игрока X");
        board = new int[3][3];
        for (Button cell : cells) {
            cell.setText("");
        }
    }
}

