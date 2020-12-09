from NaiveBayesClassifier import NaiveBayesClassifier
import numpy as np


def read_data(path):
    D = np.genfromtxt(path, delimiter=",")
    y = D[:, -1].astype('int')
    X = D[:, :-1]
    return X, y


def discretize(X, B, X_ref=None):
    if X_ref is None:
        X_ref = X
    mins = np.min(X_ref, axis=0)
    maxes = np.max(X_ref, axis=0)
    X_d = np.floor((X - mins) / (maxes - mins) * B).astype('int')
    return np.clip(X_d, 0, B - 1)


def train_test_split(X, y, train_ratio=0.75, seed=0):
    m = X.shape[0]
    np.random.seed(seed)
    indexes = np.random.permutation(m)
    X = X[indexes]
    y = y[indexes]
    split = round(train_ratio * m)
    X_train = X[:split]
    y_train = y[:split].astype('int')
    X_test = X[split:]
    y_test = y[split:].astype('int')
    return X_train, y_train, X_test, y_test


if __name__ == '__main__':
    # Zieba, M., Tomczak, S. K., & Tomczak, J. M. (2016).
    # Ensemble Boosted Trees with Synthetic Features Generation in Application to Bankruptcy Prediction.
    # Expert Systems with Applications.
    # http://archive.ics.uci.edu/ml/datasets/Polish+companies+bankruptcy+data
    X, y = read_data("data/3year.csv")

    print(X.shape)
    print(y.shape)

    X_train, y_train, X_test, y_test = train_test_split(X, y, train_ratio=0.75, seed=0)
    m, n = X_train.shape

    B = 5
    X_train_d = discretize(X_train, B)
    X_test_d = discretize(X_test, B, X_ref=X_train)

    domain_sizes = np.ones(n, dtype='int')*B

    nbc = NaiveBayesClassifier(domain_sizes=domain_sizes, laplace=True)

    nbc.fit(X_train_d, y_train)

    print(X_train_d.shape)
    predictions_train = nbc.predict(X_train_d)
    print("TRAIN ACC: " + str(nbc.score(X_train_d, y_train)))

    print(X_test_d.shape)
    predictions_test = nbc.predict(X_test_d)
    print("TEST ACC: " + str(nbc.score(X_test_d, y_test)))
