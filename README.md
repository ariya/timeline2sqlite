# Location History Extractor

This tool turns your Google Maps location history into a database.

It is crafted in [Clojure](https://clojure.org) and executed using [nbb](https://github.com/babashka/nbb) with [Node.js](https://nodejs.org). Its only dependency is [sql.js](https://www.npmjs.com/package/sql.js).

**Requirement:** [Node.js](https://nodejs.org) v18 or newer.

To install the necessary dependencies, run `npm install`.

Grab your location history from [Google Takeout](https://takeout.google.com) and unzip the file. Find those per-month timeline JSON files tucked away in `Location History/Semantic Location History/`.

Run the script on a file like this:
```bash
npm run timeline2sqlite 2023_FEBRUARY.json
```

This will generate a database named `places.sqlite`, overwriting it if it already exists. Open this file using the [sqlite CLI](https://sqlite.org/cli.html), [Datasette](https://datasette.io), or any other database management tool.