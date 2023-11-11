import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Indiana implements Explorer {
	int maxUnderwaterMoves;
	PlayerController controller;

	/**
	 * Czas (liczba kolejnych dozwolonych ruchów) pod wodą.
	 *
	 * @param moves liczba ruchów.
	 */
	@Override
	public void underwaterMovesAllowed(int moves) {
		maxUnderwaterMoves = moves;
	}

	/**
	 * Przekazanie kontrolera gracza.
	 *
	 * @param controller kontroler gracza
	 */
	@Override
	public void setPlayerController(PlayerController controller) {
		this.controller = controller;
	}

	/**
	 * Rozpoczęcie poszukiwania wyjścia. Można zacząć wykonywać metody move() z
	 * interfejsu kontroler.
	 */
	@Override
	public void findExit() {
		Map<Position, Tile> map = new HashMap<>();
		var path = new Stack<Position>();
		var pos = new Position(0, 0);
		map.put(pos, Tile.Path);

		try {
			while (true) {
				path.push(pos);
				var newPos = step(pos, map);

				if (newPos.equals(pos)) {
					path.pop();
					pos = path.pop();
					var dir = adjacentDirection(newPos, pos);

					try {
						controller.move(dir);
					} catch (Exception e) {
						throw new ExceptionInSafeMoveException();
					}

					continue;
				}

				pos = newPos;
			}
		} catch (Exit e) {
			return;
		}
	}

	Position step(Position pos, Map<Position, Tile> map) throws Exit {
		for (var dir : List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)) {
			var next = dir.step(pos);
			if (map.containsKey(next)) {
				continue;
			}

			try {
				controller.move(dir);
			} catch (OnFire e) {
				try {
					controller.move(opposite(dir));
				} catch (Exception exception) {
					throw new ExceptionInFireEscapeException();
				}

				map.put(next, Tile.Fire);
				continue;
			} catch (Flooded e) {
				map.put(next, Tile.Water);
				return next;
			} catch (Wall e) {
				map.put(next, Tile.Wall);
				continue;
			}

			map.put(next, Tile.Path);
			return next;
		}

		return pos;
	}

	Direction opposite(Direction dir) {
		switch (dir) {
			case NORTH -> {
				return Direction.SOUTH;
			}
			case SOUTH -> {
				return Direction.NORTH;
			}
			case EAST -> {
				return Direction.WEST;
			}
			case WEST -> {
				return Direction.EAST;
			}
		}

		return dir;
	}

	Direction adjacentDirection(Position from, Position to) {
		for (var dir : List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)) {
			if (dir.step(from).equals(to)) {
				return dir;
			}
		}

		throw new RuntimeException("positions are not adjacent");
	}
}

class ExceptionInSafeMoveException extends IllegalStateException { }
class ExceptionInFireEscapeException extends ExceptionInSafeMoveException { }

enum Tile {
	Wall,
	Fire,
	Water,
	Path,
	Exit;

	boolean isObstacle() {
		switch (this) {
			case Wall, Fire -> {
				return true;
			}
		}

		return false;
	}
}
