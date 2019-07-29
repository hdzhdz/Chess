package main.java.board;

import main.java.board.Board.Builder;
import main.java.pieces.Pawn;
import main.java.pieces.Piece;
import main.java.pieces.Rook;

public abstract class Move {
    protected final Board board;
    protected final Piece movedPiece;
    protected final int destinationCoordinate;
    protected boolean isFirstMove;

    public static final Move NULL_MOVE = new NullMove();

    private Move(final Board board, final Piece movedPiece, final int destinationCoordinate) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destinationCoordinate = destinationCoordinate;
        this.isFirstMove = movedPiece.isFirstMove();
    }

    private Move (final Board board, final int destinationCoordinate ){
        this.board = board;
        this.destinationCoordinate = destinationCoordinate;
        this.movedPiece = null;
        this.isFirstMove = false;
    }

    @Override
    public int hashCode() {
        System.out.println("AAAAAAAAAAAAAAAAAAAAAA");
        System.out.println(movedPiece.toString());
        int result = 1;
        result = 31 * result + this.destinationCoordinate;
        result = 31 * result + this.movedPiece.hashCode();
        result = 31 * result + this.movedPiece.getPiecePos();
        result = result + (isFirstMove ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals (final Object other){
        if (this == other){
            return true;
        }

        if (!(other instanceof Move)){
            return false;
        }
        final Move otherMove = (Move) other;
        return getDestinationCoordinate() == otherMove.getDestinationCoordinate() && getMovedPiece().equals(otherMove.getMovedPiece())
        && getCurrentCoordinate() == otherMove.getCurrentCoordinate();
    }

    public Board getBoard() {
        return this.board;
    }
    public int getCurrentCoordinate() {
        return this.movedPiece.getPiecePos();
    }
    public int getDestinationCoordinate(){
        return this.destinationCoordinate;
    }

    public Piece getMovedPiece(){
        return this.movedPiece;
    }

    public boolean isAttack (){
        return false;
    }

    public boolean isCastilingMove() {
        return false;
    }

    public Piece getAttackedPiece(){
        return null;
    }

    public Board execute(){
        final Builder builder = new Builder();
        for (final Piece piece : this.board.currentPlayer().getActivePieces()){
            if (!this.movedPiece.equals((piece))){
                builder.setPiece(piece);
            }
        }
        for (final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
            builder.setPiece((piece));
        }
        builder.setPiece(this.movedPiece.movePiece(this));
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAliance());

        return builder.build();
    }

    public static final class MajorMove extends Move {
        public MajorMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof MajorMove && super.equals(other);
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType().toString() +
                    BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static class MajorAttackMove
            extends AttackMove {

        public MajorAttackMove(final Board board,
                               final Piece pieceMoved,
                               final int destinationCoordinate,
                               final Piece pieceAttacked) {
            super(board, pieceMoved, destinationCoordinate, pieceAttacked);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof MajorAttackMove && super.equals(other);

        }

        @Override
        public String toString() {
            return movedPiece.getPieceType() + "x" +
                    BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }

    }

    public static final class PawnMove extends Move {
        public PawnMove(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnMove && super.equals(other);
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }
    public static class PawnPromotion extends Move {
        final Move decoratedMove;
        final Pawn promotedPawn;

        public PawnPromotion(final Move decoratedMove) {
            super(decoratedMove.getBoard(), decoratedMove.getMovedPiece(), decoratedMove.getDestinationCoordinate());
            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn) decoratedMove.getMovedPiece();
        }
        @Override
        public int hashCode() {
            return decoratedMove.hashCode() + (31 * promotedPawn.hashCode());
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnPromotion && (super.equals(other));
        }
        @Override
        public Board execute(){

            final Board pawnMovedBoard = this.decoratedMove.execute();
            final Board.Builder builder = new Builder();
            for (final Piece piece : pawnMovedBoard.currentPlayer().getActivePieces()){
                if (!this.promotedPawn.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece : pawnMovedBoard.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this));
            builder.setMoveMaker(pawnMovedBoard.currentPlayer().getAliance());
            return builder.build();
        }

        @Override
        public boolean isAttack() {
            return this.decoratedMove.isAttack();
        }

        @Override
        public Piece getAttackedPiece() {
            return this.decoratedMove.getAttackedPiece();
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.movedPiece.getPiecePos()) + "-" +
                    BoardUtils.getPositionAtCoordinate(this.destinationCoordinate) + "=" + this.promotedPawn.getPromotionPiece().getPieceType();
        }

    }

    public static final class PawnJump extends Move {
        public PawnJump(final Board board, final Piece movedPiece, final int destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }
        @Override
        public Board execute(){
            final Builder builder = new Builder();
            for (final Piece piece : this.board.currentPlayer().getActivePieces()){
                if (!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            final Pawn movedPawn = (Pawn) this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAliance());
            return builder.build();
        }
        @Override
        public String toString(){
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static class PawnAttackMove extends AttackMove {
        public PawnAttackMove(final Board board,
                              final Piece pieceMoved,
                              final int destinationCoordinate,
                              final Piece pieceAttacked) {
            super(board, pieceMoved, destinationCoordinate, pieceAttacked);
        }

        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }

        @Override
        public String toString() {
            return BoardUtils.getPositionAtCoordinate(this.movedPiece.getPiecePos()).substring(0, 1) + "x" +
                    BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static final class PawnEnPassantAttackMove extends AttackMove {
        public PawnEnPassantAttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate,final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate, attackedPiece);
        }
        @Override
        public boolean equals(final Object other) {
            return this == other || other instanceof PawnEnPassantAttackMove && super.equals(other);
        }
        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece : this.board.currentPlayer().getActivePieces()){
                if (!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces()){
                if(!piece.equals(this.getAttackedPiece())){
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveMaker((this.board.currentPlayer().getOpponent().getAliance()));
            return builder.build();
        }
    }

    public static class AttackMove extends Move {
        final Piece attackedPiece;

        public AttackMove(final Board board, final Piece movedPiece, final int destinationCoordinate,
                   final Piece attackedPiece) {
            super(board, movedPiece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode() {
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals (final Object other){
            if (this == other ){
                return true;
            }
            if (!(other instanceof  AttackMove)){
                return false;
            }
            final AttackMove otherAttack = (AttackMove) other;
            return super.equals(otherAttack) && getAttackedPiece().equals((otherAttack.getAttackedPiece()));
        }

        @Override
        public boolean isAttack() {
            return true;
        }

        @Override
        public Piece getAttackedPiece(){
            return this.attackedPiece;
        }
    }

    public static final class KingSideCastleMove extends CastleMove {
        public KingSideCastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate,
                                  final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart,castleRookDestination);
        }
        @Override
        public String toString(){
            return "0-0";
        }
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof KingSideCastleMove)) {
                return false;
            }
            final KingSideCastleMove otherKingSideCastleMove = (KingSideCastleMove) other;
            return super.equals(otherKingSideCastleMove) && this.castleRook.equals(otherKingSideCastleMove.getCastleRook());
        }
    }
    public static final class QueenSideCastleMove extends CastleMove {
        public QueenSideCastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate,
                                   final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate, castleRook, castleRookStart,castleRookDestination);
        }
        @Override
        public String toString(){
            return "0-0-0";
        }
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof KingSideCastleMove)) {
                return false;
            }
            final KingSideCastleMove otherKingSideCastleMove = (KingSideCastleMove) other;
            return super.equals(otherKingSideCastleMove) && this.castleRook.equals(otherKingSideCastleMove.getCastleRook());
        }
    }
    static abstract class CastleMove extends Move {
        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;
        public CastleMove(final Board board, final Piece movedPiece, final int destinationCoordinate,
                          final Rook castleRook, final int castleRookStart, final int castleRookDestination) {
            super(board, movedPiece, destinationCoordinate);
            this.castleRook  = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }
        public Rook getCastleRook() {
            return this.castleRook;
        }
        @Override
        public boolean isCastilingMove() {
            return true;
        }

        @Override
        public Board execute() {
            final Builder builder = new Builder();
            for (final Piece piece : this.board.currentPlayer().getActivePieces()){
                if (!this.movedPiece.equals(piece) && !this.castleRook.equals((piece))){
                    builder.setPiece(piece);
                }
            }
            for (final Piece piece: this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            builder.setPiece((this.movedPiece.movePiece((this))));
            builder.setPiece((new Rook (this.castleRook.getPieceAlliance(), this.castleRookDestination)));
            builder.setMoveMaker((this.board.currentPlayer().getOpponent().getAliance()));
            return builder.build();
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.castleRookDestination;
            return result;
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CastleMove)) {
                return false;
            }
            final CastleMove otherCastleMove = (CastleMove) other;
            return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
        }
    }
    public static final class NullMove extends Move {
        public NullMove() {
            super(null,-1);
        }
        @Override
        public Board execute(){
            throw new RuntimeException( "Can't execute null move~");
        }
        @Override
        public int getCurrentCoordinate(){
            return -1;
        }
    }
    public static class MoveFactory {
        private MoveFactory() {
            throw new RuntimeException("Not create");
        }
        public static Move createMove(final Board board, final int currentCoordinate, final int destinationCoordinate){
            for (final Move move : board.getAllLegalMoves()){
                if (move.getCurrentCoordinate() == currentCoordinate && move.getDestinationCoordinate() == destinationCoordinate ){
                    return move;
                }
            }
            System.out.println("NULL");
            return NULL_MOVE;
        }
        public static Move getNullMove() {
            return NULL_MOVE;
        }
    }
}
