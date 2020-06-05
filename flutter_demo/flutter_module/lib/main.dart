import 'package:english_words/english_words.dart' as prefix0;
import 'package:flutter/material.dart';
import 'package:english_words/english_words.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {

    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primaryColor: Colors.white,
      ),
//      home: MyHomePage(title: 'Flutter Demo Home Page'),
      home: MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);
  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {

  @override
  Widget build(BuildContext context) {
    final wordPair = new WordPair.random();
    return Scaffold(
      appBar: AppBar(
        title: Image.asset(
          "abc.png"
        ),
      ),
      body: Center(
        child: new RandomWords(),
      ),
    );
  }
}

class RandomWords extends StatefulWidget  {
  @override
  State<StatefulWidget> createState() {
    return new RandomWordsState();
  }
}

class RandomWordsState extends State<RandomWords> {
  final _suggestions = <WordPair>[];
  final _saved = new Set<WordPair>();
  final _biggerFont  = const TextStyle(fontSize: 18.0);
  @override
  Widget build(BuildContext context) {
    final wordPair = new WordPair.random();
//    return new Text(wordPair.asPascalCase);
    return new Scaffold(
      appBar: new AppBar(
        title: new Text("start name pair"),
        actions: <Widget>[
          new IconButton(icon: new Icon(Icons.access_alarm), onPressed: _pushSaved)
        ],
      ),
      body: _buildSuggestion(),
    );
  }

  void _pushSaved() {
    Navigator.of(context).push(
      new MaterialPageRoute(
          builder: (context) {
            final tiles = _saved.map(
                (pair) {
                  return new ListTile(
                    title: new Text(
                      pair.asPascalCase,
                      style: _biggerFont,
                    ),
                    onTap: () {
                      ///
                    },
                  );
                }
            );
            final divided = ListTile.divideTiles(
                tiles: tiles,
                context: context).toList();
            return new Scaffold(
              appBar: new AppBar(
                title: new Text("saved suggestions"),
              ),
              body: new ListView(children: divided),
            );
          })
    );
  }

  Widget _buildSuggestion() {
    return new ListView.builder(
      padding: const EdgeInsets.all(16.0),
      itemBuilder: (context,i){
        if (i.isOdd) return new Divider();

        final index = i ~/2;
        if (index >= _suggestions.length) {
          _suggestions.addAll(prefix0.generateWordPairs().take(10));
        }
        return _buildRow(_suggestions[index]);
      },
    );
  }

  Widget _buildRow(prefix0.WordPair pair) {
    final alreadSaved = _saved.contains(pair);
    return new ListTile(
      title: new Text(
        pair.asPascalCase,
        style: _biggerFont,
      ),
      trailing: new Icon(
        alreadSaved? Icons.favorite:Icons.favorite_border,
        color: alreadSaved?Colors.red:null,
      ),
      onTap: (){
        setState(() {
          if (alreadSaved) {
            _saved.remove(pair);
          }
          else {
            _saved.add(pair);
          }
        });
      },
    );
  }
}
