package com.codenjoy.dojo.snake.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.RandomDice;
import com.codenjoy.dojo.snake.model.Elements;

import javax.swing.text.Element;
import java.util.HashMap;
import java.util.List;

/**
 * User: your name
 */
public class YourSolver implements Solver<Board> {

    private Dice dice;
    private Board board;

    public YourSolver(Dice dice) {
        this.dice = dice;
    }

    public HashMap<String, Integer> distanceToObstacles() {
        int distance_left = 0;
        int distance_right = 0;
        int distance_up = 0;
        int distance_down = 0;
        HashMap<String, Integer> direction = new HashMap<>();
        for (int i = board.getSnake().size() - 1; i > 0; i--) {
            if (board.getHead().getX() < board.getSnake().get(i).getX() &&
                    board.getHead().getY() == board.getSnake().get(i).getY()) {
                distance_right = board.getSnake().get(i).getX() - board.getHead().getX();
            }
            if (board.getHead().getY() < board.getSnake().get(i).getY() &&
                    board.getHead().getX() == board.getSnake().get(i).getX()) {
                distance_up = board.getSnake().get(i).getY() - board.getHead().getY();
            }
        }
        for (int i = 1; i < board.getSnake().size(); i++) {
            if (board.getHead().getX() > board.getSnake().get(i).getX() &&
                    board.getHead().getY() == board.getSnake().get(i).getY()) {
                distance_left = board.getHead().getX() - board.getSnake().get(i).getX();
            }
            if (board.getHead().getY() > board.getSnake().get(i).getY() &&
                    board.getHead().getX() == board.getSnake().get(i).getX()) {
                distance_down = board.getHead().getY() - board.getSnake().get(i).getY();
            }
        }
        for (Point point : board.getWalls()) {
            if (distance_right == 0 && board.getHead().getY() == point.getY() && board.getHead().getX() < point.getX()) {
                distance_right = point.getX() - board.getHead().getX();
            }
            if (distance_left == 0 && board.getHead().getY() == point.getY() && board.getHead().getX() > point.getX()) {
                distance_left = board.getHead().getX() - point.getX();
            }
            if (distance_up == 0 && board.getHead().getX() == point.getX() && board.getHead().getY() < point.getY()) {
                distance_up = point.getY() - board.getHead().getY();
            }
            if (distance_down == 0 && board.getHead().getX() == point.getX() && board.getHead().getY() > point.getY()) {
                distance_down = board.getHead().getY() - point.getY();
            }
        }
       if(board.getSnake().size() < 6) {
            for (Point point : board.getStones()) {
                if (board.getHead().getY() == point.getY() && board.getHead().getX() > point.getX()) {
                    distance_left = Math.min(distance_left, board.getHead().getX() - point.getX());
                }
                if (board.getHead().getY() == point.getY() && board.getHead().getX() < point.getX()) {
                    distance_right = Math.min(distance_right, point.getX() - board.getHead().getX());
                }
                if (board.getHead().getX() == point.getX() && board.getHead().getY() < point.getY()) {
                    distance_up = Math.min(distance_up, point.getY() - board.getHead().getY());
                }
                if (board.getHead().getX() == point.getX() && board.getHead().getY() > point.getY()) {
                    distance_down = Math.min(distance_down, board.getHead().getY() - point.getY());
                }
            }
        }

        direction.put("UP", distance_up);
        direction.put("DOWN", distance_down);
        direction.put("LEFT", distance_left);
        direction.put("RIGHT", distance_right);

        return direction;
    }


    @Override
    public String get(Board board) {
        this.board = board;
        System.out.println(board.toString());
        HashMap<String, Integer> dist_to_obst = distanceToObstacles();

        if (board.getHead().getX() != board.getApples().get(0).getX()) {
            if (board.getApples().get(0).getX() > board.getHead().getX() && dist_to_obst.get("RIGHT") != 1) {
                return Direction.RIGHT.toString();
            } else if (board.getApples().get(0).getX() < board.getHead().getX() && dist_to_obst.get("LEFT") != 1) {
                return Direction.LEFT.toString();
            }
        }
        if (board.getHead().getY() != board.getApples().get(0).getY()) {
            if (board.getApples().get(0).getY() > board.getHead().getY() && dist_to_obst.get("UP") != 1) {
                return Direction.UP.toString();
            } else if (board.getApples().get(0).getY() < board.getHead().getY() && dist_to_obst.get("DOWN") != 1) {
                return Direction.DOWN.toString();
            }
        }
        if (dist_to_obst.get("UP") > 1) return Direction.UP.toString();
        else if (dist_to_obst.get("RIGHT") > 1) return Direction.RIGHT.toString();
        else if (dist_to_obst.get("DOWN") > 1) return Direction.DOWN.toString();
        else if (dist_to_obst.get("LEFT") > 1) return Direction.LEFT.toString();

        return Direction.ACT.toString();
    }

    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                "http://167.71.55.144/codenjoy-contest/board/player/upidp0d4bo619ql9afix?code=8261104463088504565",
                new YourSolver(new RandomDice()),
                new Board());
    }
}
