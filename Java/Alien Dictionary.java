H
1522007797
tags: Graph, Topological Sort, DFS, BFS, Backtracking

给一个 array of strings:  假如这个array是按照一个新的字母排序表(alien dictionary)排出来的, 需要找到这个字母排序.

有可能有多重排序的方法, 给出一种就可以.

#### BFS
- topological sort 本身很好写, 但是要在题目中先了解到字母排序的本质
- 本质: 上下两行string, 相对应的相同的index上, 如果字母不同, 就说明排在第一行的字母在字母表里更领先
- 其实上面这个排序的本质很好想, 但是把它具体化成构建graph的代码, 会稍微有点难想到
- 把 string array 变成topological sort的 graph
- 算indegree, 然后用 BFS 来找到那些 inDegree == 0的 node
- 最先inDegree == 0的node, 就排在字母表前面.
- 下面的解法, 用了Graph: map<Character, List<Character>>, 而不是  List[26], 其实更加试用超过26个字母的dictionary.

#### DFS
- 跟BFS建立 grpah 的过程一模一样
- DFS的不同在于: 用visited map 来标记走过的地方
- 走到leaf的时候, add to result: 但因为走到了底才add, 最终的顺序应该颠倒 (或者, sb.insert(0, x) 直接用颠倒的顺序add)

```

/*

There is a new alien language which uses the latin alphabet. 
However, the order among letters are unknown to you. 
You receive a list of non-empty words from the dictionary, 
where words are sorted lexicographically by the rules of this new language. 
Derive the order of letters in this language.

Example 1:
Given the following words in dictionary,

[
  "wrt",
  "wrf",
  "er",
  "ett",
  "rftt"
]
The correct order is: "wertf".

Example 2:
Given the following words in dictionary,

[
  "z",
  "x"
]
The correct order is: "zx".

Example 3:
Given the following words in dictionary,

[
  "z",
  "x",
  "z"
]
The order is invalid, so return "".

Note:
You may assume all letters are in lowercase.
You may assume that if a is a prefix of b, then a must appear before b in the given dictionary.
If the order is invalid, return an empty string.
There may be multiple valid order of letters, return any one of them is fine.

*/

/*
Thoughts:
Topological sort with BFS. The tricky part is: how to construct the node-edge relationship?
For the same index of two strings: if the word1[index] != word2[index],
that means c1 is at the leading position than c2 in topological order.

Use this feature to build the edges.

1. Build graph
2. Calculate indegree
3. BSF
*/
class Solution {
    public String alienOrder(String[] words) {
        if (words == null || words.length == 0) {
            return "";
        }

        Map<Character, List<Character>> graph = buildGraph(words);
        Map<Character, Integer> inDegree = buildInDegree(graph);
        
        // Topological sort with BFS
        return topoSort(graph, inDegree);
    }
    
    private String topoSort(Map<Character, List<Character>> graph, Map<Character, Integer> inDegree) {
        Queue<Character> queue = new LinkedList<>();
        
        // Build queue with 0 indegree
        for (char c : inDegree.keySet()) {
            if (inDegree.get(c) == 0) {
                queue.offer(c);
            }
        }

        StringBuffer sb = new StringBuffer();
        while (!queue.isEmpty()) {
            char c = queue.poll();
            sb.append(c);
            
            for (char edgeNode : graph.get(c)) {
                inDegree.put(edgeNode, inDegree.get(edgeNode) - 1);
                if (inDegree.get(edgeNode) == 0) {
                    queue.offer(edgeNode);
                }
            }
        }
        
        if (sb.length() != graph.size()) {
            return "";
        }
        
        return sb.toString();
    }
    
    private Map<Character, List<Character>> buildGraph (String[] words) {
        Map<Character, List<Character>> graph = new HashMap<>();

        // Create nodes
        for (String word: words) {
            for (char c : word.toCharArray()) {
                if (!graph.containsKey(c)) {
                    graph.put(c, new ArrayList<>());
                }
            }
        }
        
        // Build edges
        for (int i = 0; i < words.length - 1; i++) {
            int index = 0;
            while (index < words[i].length() && index < words[i + 1].length()) {
                char c = words[i].charAt(index);
                if (c != words[i + 1].charAt(index)) {
                    graph.get(c).add(words[i + 1].charAt(index));
                    break;
                }
                index++;
            }
        }

        return graph;
    }
    
    private Map<Character, Integer> buildInDegree(Map<Character, List<Character>> graph) {
        Map<Character, Integer> inDegree = new HashMap<>();

        // Build baseline
        for (char c : graph.keySet()) {
            inDegree.put(c, 0);
        }
        
        // Aggregate inDegree
        for (char c: graph.keySet()) {
            for (char edgeNode : graph.get(c)) {
                inDegree.put(edgeNode, inDegree.get(edgeNode) + 1);
            }
        }
        
        return inDegree;
    }
}



/*
Thoughts:
DFS, mark visited. When dfs down to an leaf element,
it'll be the last element of the final output. (reverse order)
*/
class Solution {
    Map<Character, List<Character>> graph = new HashMap<>();
    Map<Character, Integer> visited = new HashMap<>();
    StringBuffer sb = new StringBuffer();
    
    public String alienOrder(String[] words) {
        if (words == null || words.length == 0) {
            return "";
        }

        // Build graph, and visited map.
        buildGraph(words);
        
        // Topological sort with dfs
        for (char c : graph.keySet()) {
            if (!dfs(c)) {
                return "";
            }
        }
        
        return sb.toString();
    }
    
    private boolean dfs(Character c) {
        if (visited.get(c) == 1) {
            return true;
        }
        if (visited.get(c) == -1) {
            return false;
        }
        visited.put(c, -1);
        for (char edgeNode : graph.get(c)) {
            if (!dfs(edgeNode)) {
                return false;
            }
        }

        sb.insert(0, c);
        visited.put(c, 1);
        return true;
    }
    
    private void buildGraph (String[] words) {
        // Create nodes
        for (String word: words) {
            for (char c : word.toCharArray()) {
                if (!graph.containsKey(c)) {
                    graph.put(c, new ArrayList<>());
                    visited.put(c, 0);
                }
            }
        }
        
        // Build edges
        for (int i = 0; i < words.length - 1; i++) {
            int index = 0;
            while (index < words[i].length() && index < words[i + 1].length()) {
                char c = words[i].charAt(index);
                if (c != words[i + 1].charAt(index)) {
                    graph.get(c).add(words[i + 1].charAt(index));
                    break;
                }
                index++;
            }
        }
    }
}




```