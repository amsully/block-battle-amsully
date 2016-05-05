from Bot.Strategies.RandomStrategy import RandomStrategy
from Bot.Strategies.NaiveStrategy import NaiveStrategy
from Bot.Strategies.BestFitStrategy import BestFitStrategy
from Bot.Strategies.BestFitStrategy_2 import BestFitStrategy_2


def create(strategyType, game):
    switcher = {
        "random": RandomStrategy(game),
        "naive": NaiveStrategy(game),
        "bestfit": BestFitStrategy(game),
        "bestfit2": BestFitStrategy_2(game)
    }

    strategy = switcher.get(strategyType.lower())

    return Planner(strategy)

class Planner:
    def __init__(self, strategy):
        self._strategy = strategy

    def makeMove(self):
        return self._strategy.choose()