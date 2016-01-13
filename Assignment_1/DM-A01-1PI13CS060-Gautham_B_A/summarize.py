# Name : Gautham B A
# USN : 1PI13CS060
# Class : 6th semester
# Section : A
import copy
import csv
import random
import math

__author__ = 'gauth_000'


def compute_measures(data, headers):
    minval = [math.inf for i in range(len(data[0]) - 1)]
    maxval = [0 for i in range(len(data[0]) - 1)]
    mean = [0 for i in range(len(data[0]) - 1)]
    columns = [[] for i in range(len(data[0]) - 1)]

    # computing min, max and mean
    for row in data:
        for i in range(len(row) - 1):
            if eval(row[i]) < minval[i]:
                minval[i] = eval(row[i])
            if eval(row[i]) > maxval[i]:
                maxval[i] = eval(row[i])
            mean[i] += eval(row[i])
            columns[i].append(row[i])
    mean = [round(num / len(data), 4) for num in mean]

    # computing median
    median = []
    mid = (len(data) + 1) // 2
    if len(data) % 2:
        median = [round(eval(sorted(columns[i])[mid - 1]), 4) for i in range(len(columns))]
    else:
        for i in range(len(columns)):
            median_ = sorted(map(eval, columns[i]))
            median.append(round((median_[mid - 1] + median_[mid]) / 2, 4))

    # computing standard deviation
    stdev = copy.deepcopy(columns)
    for col in range(len(stdev)):
        for i in range(len(stdev[col])):
            stdev[col][i] = (eval(stdev[col][i]) - mean[col]) ** 2
        stdev[col] = round((sum(stdev[col]) / len(stdev[col])) ** 0.5, 4)

    print('{: <23} {: <23} {: <23} {: <23} {: <23} '.format(*headers[:-1]))
    print('min={: <20} min={: <20} min={: <20} min={: <20} min={: <20}'.format(*minval))
    print('max={: <20} max={: <20} max={: <20} max={: <20} max={: <20}'.format(*maxval))
    print('mean={: <19} mean={: <19} mean={: <19} mean={: <19} mean={: <19}'.format(*mean))
    print('median={: <17} median={: <17} median={: <17} median={: <17} median={: <17}'.format(*median))
    print('std. dev={: <15} std. dev={: <15} std. dev={: <15} std. dev={: <15} std. dev={: <15}'.format(*stdev))
    print()


def classify_species(data):
    species_ = []
    species = []
    i = 0
    species_.append(data[0])
    for j in range(1, len(data)):
        if data[j][len(data[j]) - 1] == data[i][len(data[i]) - 1]:
            species_.append(data[j])
        else:
            species.append(species_)
            species_ = [data[j]]
        i += 1
    species.append(species_)
    return species


def compute_distances(data):
    minval = [math.inf for i in range(3)]
    maxval = [0 for i in range(3)]
    emin = mmin = smin = 0
    emax = mmax = smax = 0
    for i in range(len(data)):
        for j in range(i + 1, len(data)):
            etemp = mtemp = 0
            stemp = []
            for v in list(zip(data[i], data[j]))[1:-1]:
                etemp += (eval(v[0]) - eval(v[1])) ** 2
                mtemp += abs(eval(v[0]) - eval(v[1]))
                stemp.append(abs(eval(v[0]) - eval(v[1])))
            etemp **= 0.5
            stemp = max(stemp)
            if etemp < minval[0]:
                minval[0] = etemp
                emin = (round(etemp, 2), data[i][0] + '(' + data[i][len(data[i]) - 1] + ')',
                        data[j][0] + '(' + data[j][len(data[j]) - 1] + ')')
            if etemp > maxval[0]:
                maxval[0] = etemp
                emax = (round(etemp, 2), data[i][0] + '(' + data[i][len(data[i]) - 1] + ')',
                        data[j][0] + '(' + data[j][len(data[j]) - 1] + ')')

            if mtemp < minval[1]:
                minval[1] = mtemp
                mmin = (round(mtemp, 2), data[i][0] + '(' + data[i][len(data[i]) - 1] + ')',
                        data[j][0] + '(' + data[j][len(data[j]) - 1] + ')')
            if mtemp > maxval[1]:
                maxval[1] = mtemp
                mmax = (round(mtemp, 2), data[i][0] + '(' + data[i][len(data[i]) - 1] + ')',
                        data[j][0] + '(' + data[j][len(data[j]) - 1] + ')')

            if stemp < minval[2]:
                minval[2] = stemp
                smin = (round(stemp, 2), data[i][0] + '(' + data[i][len(data[i]) - 1] + ')',
                        data[j][0] + '(' + data[j][len(data[j]) - 1] + ')')
            if stemp > maxval[2]:
                maxval[2] = stemp
                smax = (round(stemp, 2), data[i][0] + '(' + data[i][len(data[i]) - 1] + ')',
                        data[j][0] + '(' + data[j][len(data[j]) - 1] + ')')

    print('{: <30} {: <30} {: <30} {: <30}'.format(*(' ', 'eucledian', 'manhattan', 'supremum')))
    print('minimum=', '{: <40} {: <40} {: <40}'.format(
        *(', '.join(map(str, emin)), ', '.join(map(str, mmin)), ', '.join(map(str, smin)))))
    print('maximum=', '{: <40} {: <40} {: <40}'.format(
        *(', '.join(map(str, emax)), ', '.join(map(str, mmax)), ', '.join(map(str, smax)))))


def nearest_neighbours(key, data):
    edata = []
    mdata = []
    sdata = []
    for row in data:
        edist = mdist = 0
        sdist = []
        for v in list(zip(key, row))[1:-1]:
            edist += (eval(v[0]) - eval(v[1])) ** 2
            mdist += abs(eval(v[0]) - eval(v[1]))
            sdist.append(abs(eval(v[0]) - eval(v[1])))
        edist **= 0.5
        sdist = max(sdist)
        edata.append((edist, row[-1]))
        mdata.append((mdist, row[-1]))
        sdata.append((sdist, row[-1]))
    edata.sort()
    mdata.sort()
    sdata.sort()
    return edata, mdata, sdata


def k_nearest(k, testdata, traineddata):
    accuracies=[0, 0, 0]
    print_row(k, ('test_id', 'actual_class', 'manhattan', 'eucledian', 'supremum'))
    for row in testdata:
        distances = nearest_neighbours(row, traineddata)
        prediction=print_format(k, (row[0], row[-1], distances[1][:k], distances[0][:k], distances[2][:k]))
        for i in range(len(prediction)):
            if row[-1]==prediction[i]:
                accuracies[i]+=1
    accuracies=[num/len(testdata) for num in accuracies]
    print('accuracy:', '\neucledian=', accuracies[1], '\nmanhattan=', accuracies[0], '\nsupremum=', accuracies[2])
    print()


def print_format(k, data):
    datalist = []
    datalist += data[:2]
    specieslist = []
    for tuplelist in data[2:]:
        line = []
        for tuple_ in tuplelist:
            line.append(tuple_[1])
        datalist.append('neighbours:' + ', '.join(line))
        specieslist.append(line)
    print_row(k, datalist)
    especies = predict_species(specieslist[0])
    mspecies = predict_species(specieslist[1])
    sspecies = predict_species(specieslist[2])
    print_row(k, (' ', ' ', 'prediction:' + especies, 'prediction:' + mspecies, 'prediction:' + sspecies))
    return especies, mspecies, sspecies


def predict_species(species):
    return max(set(species), key=species.count)


def print_row(k, data):
    sp = k * 10 + 20
    space = '{: <10} {: <15} {: <' + str(sp) + '} {: <' + str(sp) + '} {: <' + str(sp) + '}'
    print(space.format(*data))


with open('iris.csv') as file:
    data = [row for row in csv.reader(file, delimiter=',')]
    testdata = []
    for num in random.sample(range(1, len(data) - 10), 10):
        testdata.append(data[num])
        del data[num]
    testdata = sorted(testdata, key=lambda x: x[-1])
    print('step-1 : printing id')
    for row in testdata:
        print(row[0])
    print('\nstep-2 : summary statistics')
    compute_measures(testdata, data[0])
    for class_ in classify_species(testdata):
        print('class', class_[0][len(class_[0]) - 1], ':')
        compute_measures(class_, data[0])
    compute_distances(testdata)
    print('\nstep-3 : class prediction using k-nearest neighbour approach')
    for k in range(1, 6):
        print('k :', k)
        k_nearest(k, testdata, data[1:])