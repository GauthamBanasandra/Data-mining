# Name : Gautham B A
# USN : 1PI13CS060


class Node:
    graph = [[]]
    clusters = 0
    clusterCores = {}

    # attributes of each node
    def __init__(self, label=None, is_core=False, is_visited=False):
        self.epsilonNeighbours = {}
        self.isVisited = is_visited
        self.isCore = is_core
        self.label = label
        self.cluster = set()
        self.neighbours = set()
        self.coreNode = None

    # finds all the neighbours of this node in the graph
    def find_neighbours(self):
        self.neighbours.add(self.label)
        for i in range(len(Node.graph[self.label])):
            if Node.graph[self.label][i] == 1:
                self.neighbours.add(i)

    # compute the similarity of the current node w.r.t. its neighbouring nodes
    def compute_similarity(self):
        for neighbour in self.neighbours:
            if neighbour != self.label:
                neighbour_node = nodes[neighbour].neighbours
                numerator = len(self.neighbours.intersection(neighbour_node))
                denominator = (len(self.neighbours) * len(neighbour_node)) ** 0.5
                self.epsilonNeighbours[neighbour] = round(numerator / denominator, 2)

    # determine if the current node can become a core node, based on its popularity
    def determine_core(self):
        popularity = 0
        non_epsilon = []
        for key, value in self.epsilonNeighbours.items():
            if value >= epsilon:
                popularity += 1
            else:
                non_epsilon.append(key)
        for key in non_epsilon:
            del self.epsilonNeighbours[key]
        if self.coreNode is None:
            if popularity >= popularity_threshold:
                self.isCore = True
                Node.clusters += 1
                Node.clusterCores[Node.clusters] = self.label
                self.cluster.add(Node.clusters)
                self.coreNode = self.label
                self.set_core()
        else:
            self.set_core()

    # setting the core node of its epsilon neighbours
    def set_core(self):
        for key, value in self.epsilonNeighbours.items():
            if value >= epsilon and not nodes[key].isCore:
                nodes[key].coreNode = self.coreNode
                nodes[key].cluster.add(Node.clusters)

    def __str__(self, *args, **kwargs):
        return '{:<7}{:<10}{:<15}{:<50}{:<25}'.format(self.label,
                                                      str(self.isCore),
                                                      str(self.cluster),
                                                      str(self.epsilonNeighbours),
                                                      str(self.neighbours))


epsilon = float(input())
popularity_threshold = int(input())
size = int(input())

graph = []
for k in range(size):
    graph.append(list(map(int, raw_input())))

Node.graph = graph

nodes = []
for p in range(len(graph)):
    node = Node(p)
    node.find_neighbours()
    nodes.append(node)

for node in nodes:
    node.compute_similarity()
    node.determine_core()

hubs = set()
outliers = set()
clusterMembers = {}
for node in nodes:
    for c in node.cluster:
        if c not in clusterMembers.keys():
            clusterMembers[c] = {node.label}
        else:
            clusterMembers[c].add(node.label)
    if len(node.cluster) > 1:
        hubs.add(node.label)
    elif len(node.cluster) == 0:
        outliers.add(node.label)

print 'clusters:'
print '{:<15}{:<10}{:<10}'.format('cluster_id', 'core node', 'cluster members')
for k, v in Node.clusterCores.items():
    print '{:<15}{:<10}{:<10}'.format(k, v, str(clusterMembers[k]))

print 'hubs:', hubs
print 'outliers:', outliers

print('-' * 100)
print('detailed information:')
print('note-')
print('label-label(id) of the node')
print('isCore-is the node a core node')
print('cluster-clusters to which this node belongs to')
print('epsilon neighbours-neighbouring nodes having minimum epsilon value {neighbouring node:epsilon value}')
print('neighbours-neighbouring nodes of the current node')

print('\n{:<7}{:<10}{:<15}{:<50}{:<25}'.format('label', 'isCore', 'cluster', 'epsilon neighbours', 'neighbours'))
for node in nodes:
    print(node)
