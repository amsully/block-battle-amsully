import random as r
from random import randint
from AbstractStrategy import AbstractStrategy


class BestFitStrategy(AbstractStrategy):
    def __init__(self, game):
        AbstractStrategy.__init__(self, game)
        self._actions = ['left', 'right', 'turnleft', 'turnright', 'down', 'drop']

    def choose(self):
        # w = open('python_stdout.txt','a')

        moves = []

        # return self.test()

        orientation_coordinates = self.bestFit()
        shift = orientation_coordinates[1] -3

        for i in range(orientation_coordinates[0]):
            moves.append('turnright')

        move = 'down'

        if(shift >= 0):
            move = 'right'
        else:
            move = 'left'

        for i in range(abs(shift)):
            moves.append(move)

        moves.append('drop')

        # w.write('\n' + str(moves))

        return moves

    def bestFit(self):
        # w = open('python_stdout.txt','a')

        field = self._game.me.field
        piece = self._game.piece

        flip = 0
        shift = 0

        for y in range(19,-1,-1):
            for x in range(10):
                orientation_coordinates = self.fitWithOffset_anyOrientation(y, x, piece, field)

                if(orientation_coordinates != None ):
                    return orientation_coordinates

        return [0,0,0]

    def fitWithOffset_anyOrientation(self, y, x, piece, field):
        # w = open('python_stdout.txt','a')

        for orientation in range(len(piece._rotations)):
            piece_position = piece._rotations[orientation]


            fits = field.pieceFits(piece_position, [x,y])
            if fits:
                return [orientation, x, y]

        return None

    # def test(self):
    #     w = open('python_stdout.txt', 'a')

    #     moves = []

    #     moves.append('left')
    #     moves.append('left')

    #     w.write(str(self._game.me.field.field))
    #     w.write('\n'  + str(self._game.me.field.field[0]))
    #     return moves