import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Indiana implements Explorer {
	int maxUnderwaterMoves;
	int currentUnderwaterMoves = 0;
	boolean isUnderwater = false;
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

				if (currentUnderwaterMoves * 2 + 1 > maxUnderwaterMoves) {
					while (map.get(newPos).equals(Tile.Water)) {
						pos = path.pop();
						var dir = adjacentDirection(newPos, pos);
						newPos = pos;

						try {
							controller.move(dir);
						} catch (Flooded ignored) {
						} catch (Exception e) {
							throw new ExceptionInSafeMoveException();
						}
					}

					isUnderwater = false;
					currentUnderwaterMoves = 0;
					continue;
				}

				if (newPos.equals(pos)) {
					path.pop();
					pos = path.pop();
					var dir = adjacentDirection(newPos, pos);

					try {
						if (isUnderwater) {
							currentUnderwaterMoves++;
						} else {
							currentUnderwaterMoves = 0;
						}

						isUnderwater = false;
						controller.move(dir);
					} catch (Flooded e) {
						isUnderwater = true;
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
				if (isUnderwater) {
					currentUnderwaterMoves++;
				} else {
					currentUnderwaterMoves = 0;
				}

				controller.move(dir);
			} catch (OnFire e) {
				try {
					isUnderwater = false;
					controller.move(opposite(dir));
				} catch (Flooded f) {
					currentUnderwaterMoves = 0;
					isUnderwater = true;
				} catch (Exception exception) {
					throw new ExceptionInFireEscapeException();
				}

				map.put(next, Tile.Fire);
				continue;
			} catch (Flooded e) {
				map.put(next, Tile.Water);
				map.put(nextClockwise(dir).step(next), Tile.Wall);
				map.put(opposite(nextClockwise(dir)).step(next), Tile.Wall);
				isUnderwater = true;
				return next;
			} catch (Wall e) {
				map.put(next, Tile.Wall);
				continue;
			}

			map.put(next, Tile.Path);
			isUnderwater = false;
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

	Direction nextClockwise(Direction dir) {
		switch (dir) {
			case NORTH -> {
				return Direction.EAST;
			}
			case EAST -> {
				return Direction.SOUTH;
			}
			case SOUTH -> {
				return Direction.WEST;
			}
			case WEST -> {
				return Direction.NORTH;
			}
		}

		return dir;
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
