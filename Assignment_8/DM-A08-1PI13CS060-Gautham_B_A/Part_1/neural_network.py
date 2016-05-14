# Name : Gautham B A
# USN : 1PI13CS060
import random
import math


# Template for a node
class Node:
    def __init__(self, name, nodetype):
        self.name = name
        self.nodetype = nodetype

    def __str__(self, *args, **kwargs):
        return 'name: ' + self.name + '\ttype: ' + self.nodetype


# Template for an input node
class InputNode(Node):
    def __init__(self, name, nodetype, inputval):
        super().__init__(name, nodetype)
        self.inputval = inputval

    def __str__(self, *args, **kwargs):
        return super().__str__(*args, **kwargs) + '\tinput_val: ' + str(self.inputval)


# Template for a hidden node
class HiddenNode(Node):
    def __init__(self, name, nodetype, bias, error, output):
        super().__init__(name, nodetype)
        self.bias = bias
        self.error = error
        self.output = output

    def __str__(self, *args, **kwargs):
        return super().__str__(*args, **kwargs) + '\tbias: ' + str(self.bias) + '\terror: ' + str(
            self.error) + '\toutput: ' + str(self.output)

    def compute_input(self):
        inputval = 0
        for points, link in filter(lambda x: x[1].vertices[1] == int(self.name), links.items()):
            inputval += link.weight * hiddenNodes[points[0]].output if isinstance(self, OutputNode) else inputNodes[
                points[0]].inputval
        return inputval + self.bias

    def compute_output(self):
        self.output = math.tanh(self.compute_input())
        return self.output

    def compute_error(self, output):
        error = 0
        for points, link in filter(lambda x: x[1].vertices[0] == int(self.name), links.items()):
            error += link.weight * outputNodes[points[1]].error
        self.error = error * math.acos(output) ** 2
        return self.error

    def rectify_bias(self):
        self.bias += lRate * self.error
        return self.bias


# Template for an output node
class OutputNode(HiddenNode):
    def __init__(self, name, nodetype, bias, error, output, target):
        super().__init__(name, nodetype, bias, error, output)
        self.target = target

    def __str__(self, *args, **kwargs):
        return super().__str__(*args, **kwargs) + '\ttarget: ' + str(self.target)

    def compute_error(self, output):
        self.error = (self.target - self.output) * math.acos(output) ** 2
        return self.error


# Template for a link
class Link:
    def __init__(self, name, weight, *vertices):
        self.name = name
        self.weight = weight
        self.vertices = vertices[0]

    def __str__(self, *args, **kwargs):
        return 'name: ' + self.name + '\tweight: ' + str(self.weight) + '\tvertices:' + str(self.vertices)

    def rectify_link(self):
        nodes = {**inputNodes, **hiddenNodes, **outputNodes}
        try:
            self.weight += lRate * nodes[self.vertices[1]].error * nodes[self.vertices[0]].output
        except AttributeError:
            self.weight += lRate * nodes[self.vertices[1]].error * nodes[self.vertices[0]].inputval
        return self.weight


# Configurations for the neural network
lRate = 0.9
iterations = 1000

# Building input nodes.
inputNodesLen = 3
inputNodes = {}
inputs = [1, 0.25, -0.5]
for i in range(inputNodesLen):
    index = i + 1
    inputNodes[index] = InputNode(str(index), 'input', inputs[i])

# Building hidden nodes.
hiddenNodesLen = 2
hiddenNodes = {}
for i in range(hiddenNodesLen):
    index = i + inputNodesLen + 1
    hiddenNodes[index] = HiddenNode(str(index), 'hidden', random.uniform(-1, 1), 0, 0)

# Building output nodes.
outputNodesLen = 2
outputNodes = {}
target = [1, -1]
for i in range(outputNodesLen):
    index = i + inputNodesLen + hiddenNodesLen + 1
    outputNodes[index] = OutputNode(str(index), 'output', random.uniform(-1, 1), 0, 0, target[i])

# Building links.
linksLen = hiddenNodesLen * (inputNodesLen + outputNodesLen)
links = {}
for i in range(inputNodesLen):
    for j in range(hiddenNodesLen):
        index = (i + 1, j + inputNodesLen + 1)
        links[index] = Link('l' + str(index[0]) + str(index[1]), random.uniform(-1, 1), index)

for i in range(hiddenNodesLen):
    for j in range(outputNodesLen):
        index = (i + inputNodesLen + 1, j + inputNodesLen + hiddenNodesLen + 1)
        links[index] = Link('l' + str(index[0]) + str(index[1]), random.uniform(-1, 1), index)


def print_elements(elements):
    for element in elements.values():
        print(element)


# Printing the graph
def print_graph():
    print('Nodes:')
    print_elements(inputNodes)
    print_elements(hiddenNodes)
    print_elements(outputNodes)
    print('\nLinks:')
    print_elements(links)


print('initial values of graph elements:')
print_graph()


def compute_output(nodes):
    nodeOutputs = []
    for k, node in nodes.items():
        nodeOutputs.append((k, node.compute_output()))
    return nodeOutputs


def compute_error(nodes, outputs):
    for output in outputs:
        nodes[output[0]].compute_error(output[1])


# Process of prediction using neural network
for i in range(iterations):
    hiddenNodeOutput = compute_output(hiddenNodes)
    outputNodeOutput = compute_output(outputNodes)

    compute_error(outputNodes, outputNodeOutput)
    compute_error(hiddenNodes, hiddenNodeOutput)

    for link in links.values():
        link.rectify_link()

    for node in {**hiddenNodes, **outputNodes}.values():
        node.rectify_bias()

print('\nnumber of iterations:', iterations)
print('\ntermination criterion:', iterations, 'iterations')
print('\nfinal values and outputs of graph elements:')
print_graph()
