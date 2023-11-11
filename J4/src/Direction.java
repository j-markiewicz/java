/**
 * Kierunki ruchu
 */
public enum Direction {
	/**
	 * Na północ
	 */
	NORTH {
		@Override
		public Position step(Position currentPosition) {
			return new Position(currentPosition.col(), currentPosition.row() + 1);
		}
	},
	/**
	 * Na południe
	 */
	SOUTH {
		@Override
		public Position step(Position currentPosition) {
			return new Position(currentPosition.col(), currentPosition.row() - 1);
		}
	},
	/**
	 * Na wschód
	 */
	EAST {
		@Override
		public Position step(Position currentPosition) {
			return new Position(currentPosition.col() + 1, currentPosition.row());
		}

	},
	/**
	 * Na zachód
	 */
	WEST {
		@Override
		public Position step(Position currentPosition) {
			return new Position(currentPosition.col() - 1, currentPosition.row());
		}
	};

	/**
	 * Zwraca położenie po wykonaniu ruchu w danym kierunku
	 * @param currentPosition aktualne położenie
	 * @return następne położenie
	 */
	abstract public Position step(Position currentPosition);
}
