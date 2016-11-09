SystemJS.config({
  paths: {
    "github:": "jspm_packages/github/",
    "npm:": "jspm_packages/npm/",
    "server/": ""
  },
  browserConfig: {
    "baseURL": "/"
  },
  devConfig: {
    "map": {
      "plugin-typescript": "github:frankwallis/plugin-typescript@5.2.9"
    },
    "packages": {
      "github:frankwallis/plugin-typescript@5.2.9": {
        "map": {
          "typescript": "npm:typescript@2.1.1"
        }
      }
    }
  },
  transpiler: "plugin-typescript",
  packages: {
    "server": {
      "main": "boot.ts",
      "format": "esm",
      "meta": {
        "*.ts": {
          "loader": "plugin-typescript"
        }
      }
    }
  }
});

SystemJS.config({
  packageConfigPaths: [
    "github:*/*.json",
    "npm:@*/*.json",
    "npm:*.json"
  ],
  map: {
    "asn1": "npm:asn1@0.2.3",
    "assert": "npm:jspm-nodelibs-assert@0.2.0",
    "buffer": "npm:jspm-nodelibs-buffer@0.2.0",
    "child_process": "npm:jspm-nodelibs-child_process@0.2.0",
    "compression": "npm:compression@1.6.2",
    "constants": "npm:jspm-nodelibs-constants@0.2.0",
    "crypto": "npm:jspm-nodelibs-crypto@0.2.0",
    "domain": "npm:jspm-nodelibs-domain@0.2.0",
    "ejs": "npm:ejs@2.5.2",
    "events": "npm:jspm-nodelibs-events@0.2.0",
    "express": "npm:express@4.14.0",
    "fs": "npm:jspm-nodelibs-fs@0.2.0",
    "http": "npm:jspm-nodelibs-http@0.2.0",
    "json": "github:systemjs/plugin-json@0.2.2",
    "module": "npm:jspm-nodelibs-module@0.2.0",
    "net": "npm:jspm-nodelibs-net@0.2.0",
    "opn": "npm:opn@4.0.2",
    "os": "npm:jspm-nodelibs-os@0.2.0",
    "path": "npm:jspm-nodelibs-path@0.2.1",
    "process": "npm:jspm-nodelibs-process@0.2.0",
    "querystring": "npm:jspm-nodelibs-querystring@0.2.0",
    "sitemap": "npm:sitemap@1.8.2",
    "source-map-support": "npm:source-map-support@0.4.6",
    "stream": "npm:jspm-nodelibs-stream@0.2.0",
    "string_decoder": "npm:jspm-nodelibs-string_decoder@0.2.0",
    "tty": "npm:jspm-nodelibs-tty@0.2.0",
    "url": "npm:jspm-nodelibs-url@0.2.0",
    "util": "npm:jspm-nodelibs-util@0.2.1",
    "vm": "npm:jspm-nodelibs-vm@0.2.0",
    "yargs": "npm:yargs@6.3.0",
    "zlib": "npm:jspm-nodelibs-zlib@0.2.0"
  },
  packages: {
    "npm:jspm-nodelibs-os@0.2.0": {
      "map": {
        "os-browserify": "npm:os-browserify@0.2.1"
      }
    },
    "npm:jspm-nodelibs-crypto@0.2.0": {
      "map": {
        "crypto-browserify": "npm:crypto-browserify@3.11.0"
      }
    },
    "npm:crypto-browserify@3.11.0": {
      "map": {
        "create-hash": "npm:create-hash@1.1.2",
        "browserify-sign": "npm:browserify-sign@4.0.0",
        "create-ecdh": "npm:create-ecdh@4.0.0",
        "create-hmac": "npm:create-hmac@1.1.4",
        "browserify-cipher": "npm:browserify-cipher@1.0.0",
        "inherits": "npm:inherits@2.0.3",
        "pbkdf2": "npm:pbkdf2@3.0.9",
        "public-encrypt": "npm:public-encrypt@4.0.0",
        "diffie-hellman": "npm:diffie-hellman@5.0.2",
        "randombytes": "npm:randombytes@2.0.3"
      }
    },
    "npm:browserify-sign@4.0.0": {
      "map": {
        "create-hash": "npm:create-hash@1.1.2",
        "create-hmac": "npm:create-hmac@1.1.4",
        "inherits": "npm:inherits@2.0.3",
        "parse-asn1": "npm:parse-asn1@5.0.0",
        "browserify-rsa": "npm:browserify-rsa@4.0.1",
        "elliptic": "npm:elliptic@6.3.2",
        "bn.js": "npm:bn.js@4.11.6"
      }
    },
    "npm:create-hmac@1.1.4": {
      "map": {
        "create-hash": "npm:create-hash@1.1.2",
        "inherits": "npm:inherits@2.0.3"
      }
    },
    "npm:create-hash@1.1.2": {
      "map": {
        "inherits": "npm:inherits@2.0.3",
        "cipher-base": "npm:cipher-base@1.0.3",
        "ripemd160": "npm:ripemd160@1.0.1",
        "sha.js": "npm:sha.js@2.4.5"
      }
    },
    "npm:public-encrypt@4.0.0": {
      "map": {
        "create-hash": "npm:create-hash@1.1.2",
        "randombytes": "npm:randombytes@2.0.3",
        "parse-asn1": "npm:parse-asn1@5.0.0",
        "browserify-rsa": "npm:browserify-rsa@4.0.1",
        "bn.js": "npm:bn.js@4.11.6"
      }
    },
    "npm:pbkdf2@3.0.9": {
      "map": {
        "create-hmac": "npm:create-hmac@1.1.4"
      }
    },
    "npm:diffie-hellman@5.0.2": {
      "map": {
        "randombytes": "npm:randombytes@2.0.3",
        "miller-rabin": "npm:miller-rabin@4.0.0",
        "bn.js": "npm:bn.js@4.11.6"
      }
    },
    "npm:create-ecdh@4.0.0": {
      "map": {
        "elliptic": "npm:elliptic@6.3.2",
        "bn.js": "npm:bn.js@4.11.6"
      }
    },
    "npm:parse-asn1@5.0.0": {
      "map": {
        "create-hash": "npm:create-hash@1.1.2",
        "pbkdf2": "npm:pbkdf2@3.0.9",
        "evp_bytestokey": "npm:evp_bytestokey@1.0.0",
        "browserify-aes": "npm:browserify-aes@1.0.6",
        "asn1.js": "npm:asn1.js@4.9.0"
      }
    },
    "npm:browserify-cipher@1.0.0": {
      "map": {
        "browserify-des": "npm:browserify-des@1.0.0",
        "evp_bytestokey": "npm:evp_bytestokey@1.0.0",
        "browserify-aes": "npm:browserify-aes@1.0.6"
      }
    },
    "npm:cipher-base@1.0.3": {
      "map": {
        "inherits": "npm:inherits@2.0.3"
      }
    },
    "npm:browserify-rsa@4.0.1": {
      "map": {
        "randombytes": "npm:randombytes@2.0.3",
        "bn.js": "npm:bn.js@4.11.6"
      }
    },
    "npm:elliptic@6.3.2": {
      "map": {
        "inherits": "npm:inherits@2.0.3",
        "bn.js": "npm:bn.js@4.11.6",
        "hash.js": "npm:hash.js@1.0.3",
        "brorand": "npm:brorand@1.0.6"
      }
    },
    "npm:sha.js@2.4.5": {
      "map": {
        "inherits": "npm:inherits@2.0.3"
      }
    },
    "npm:browserify-aes@1.0.6": {
      "map": {
        "cipher-base": "npm:cipher-base@1.0.3",
        "create-hash": "npm:create-hash@1.1.2",
        "evp_bytestokey": "npm:evp_bytestokey@1.0.0",
        "inherits": "npm:inherits@2.0.3",
        "buffer-xor": "npm:buffer-xor@1.0.3"
      }
    },
    "npm:browserify-des@1.0.0": {
      "map": {
        "cipher-base": "npm:cipher-base@1.0.3",
        "inherits": "npm:inherits@2.0.3",
        "des.js": "npm:des.js@1.0.0"
      }
    },
    "npm:evp_bytestokey@1.0.0": {
      "map": {
        "create-hash": "npm:create-hash@1.1.2"
      }
    },
    "npm:miller-rabin@4.0.0": {
      "map": {
        "bn.js": "npm:bn.js@4.11.6",
        "brorand": "npm:brorand@1.0.6"
      }
    },
    "npm:asn1.js@4.9.0": {
      "map": {
        "bn.js": "npm:bn.js@4.11.6",
        "inherits": "npm:inherits@2.0.3",
        "minimalistic-assert": "npm:minimalistic-assert@1.0.0"
      }
    },
    "npm:hash.js@1.0.3": {
      "map": {
        "inherits": "npm:inherits@2.0.3"
      }
    },
    "npm:des.js@1.0.0": {
      "map": {
        "inherits": "npm:inherits@2.0.3",
        "minimalistic-assert": "npm:minimalistic-assert@1.0.0"
      }
    },
    "npm:jspm-nodelibs-stream@0.2.0": {
      "map": {
        "stream-browserify": "npm:stream-browserify@2.0.1"
      }
    },
    "npm:stream-browserify@2.0.1": {
      "map": {
        "inherits": "npm:inherits@2.0.3",
        "readable-stream": "npm:readable-stream@2.1.5"
      }
    },
    "npm:readable-stream@2.1.5": {
      "map": {
        "inherits": "npm:inherits@2.0.3",
        "process-nextick-args": "npm:process-nextick-args@1.0.7",
        "buffer-shims": "npm:buffer-shims@1.0.0",
        "util-deprecate": "npm:util-deprecate@1.0.2",
        "isarray": "npm:isarray@1.0.0",
        "string_decoder": "npm:string_decoder@0.10.31",
        "core-util-is": "npm:core-util-is@1.0.2"
      }
    },
    "npm:jspm-nodelibs-buffer@0.2.0": {
      "map": {
        "buffer-browserify": "npm:buffer@4.9.1"
      }
    },
    "npm:jspm-nodelibs-string_decoder@0.2.0": {
      "map": {
        "string_decoder-browserify": "npm:string_decoder@0.10.31"
      }
    },
    "npm:buffer@4.9.1": {
      "map": {
        "isarray": "npm:isarray@1.0.0",
        "ieee754": "npm:ieee754@1.1.8",
        "base64-js": "npm:base64-js@1.2.0"
      }
    },
    "npm:express@4.14.0": {
      "map": {
        "accepts": "npm:accepts@1.3.3",
        "depd": "npm:depd@1.1.0",
        "content-disposition": "npm:content-disposition@0.5.1",
        "on-finished": "npm:on-finished@2.3.0",
        "cookie": "npm:cookie@0.3.1",
        "array-flatten": "npm:array-flatten@1.1.1",
        "etag": "npm:etag@1.7.0",
        "methods": "npm:methods@1.1.2",
        "fresh": "npm:fresh@0.3.0",
        "debug": "npm:debug@2.2.0",
        "qs": "npm:qs@6.2.0",
        "type-is": "npm:type-is@1.6.13",
        "content-type": "npm:content-type@1.0.2",
        "encodeurl": "npm:encodeurl@1.0.1",
        "vary": "npm:vary@1.1.0",
        "cookie-signature": "npm:cookie-signature@1.0.6",
        "range-parser": "npm:range-parser@1.2.0",
        "merge-descriptors": "npm:merge-descriptors@1.0.1",
        "send": "npm:send@0.14.1",
        "proxy-addr": "npm:proxy-addr@1.1.2",
        "serve-static": "npm:serve-static@1.11.1",
        "escape-html": "npm:escape-html@1.0.3",
        "parseurl": "npm:parseurl@1.3.1",
        "utils-merge": "npm:utils-merge@1.0.0",
        "finalhandler": "npm:finalhandler@0.5.0",
        "path-to-regexp": "npm:path-to-regexp@0.1.7"
      }
    },
    "npm:send@0.14.1": {
      "map": {
        "debug": "npm:debug@2.2.0",
        "depd": "npm:depd@1.1.0",
        "encodeurl": "npm:encodeurl@1.0.1",
        "escape-html": "npm:escape-html@1.0.3",
        "etag": "npm:etag@1.7.0",
        "fresh": "npm:fresh@0.3.0",
        "on-finished": "npm:on-finished@2.3.0",
        "range-parser": "npm:range-parser@1.2.0",
        "mime": "npm:mime@1.3.4",
        "http-errors": "npm:http-errors@1.5.0",
        "destroy": "npm:destroy@1.0.4",
        "ms": "npm:ms@0.7.1",
        "statuses": "npm:statuses@1.3.0"
      }
    },
    "npm:serve-static@1.11.1": {
      "map": {
        "encodeurl": "npm:encodeurl@1.0.1",
        "escape-html": "npm:escape-html@1.0.3",
        "parseurl": "npm:parseurl@1.3.1",
        "send": "npm:send@0.14.1"
      }
    },
    "npm:accepts@1.3.3": {
      "map": {
        "mime-types": "npm:mime-types@2.1.12",
        "negotiator": "npm:negotiator@0.6.1"
      }
    },
    "npm:type-is@1.6.13": {
      "map": {
        "mime-types": "npm:mime-types@2.1.12",
        "media-typer": "npm:media-typer@0.3.0"
      }
    },
    "npm:finalhandler@0.5.0": {
      "map": {
        "debug": "npm:debug@2.2.0",
        "escape-html": "npm:escape-html@1.0.3",
        "on-finished": "npm:on-finished@2.3.0",
        "statuses": "npm:statuses@1.3.0",
        "unpipe": "npm:unpipe@1.0.0"
      }
    },
    "npm:on-finished@2.3.0": {
      "map": {
        "ee-first": "npm:ee-first@1.1.1"
      }
    },
    "npm:proxy-addr@1.1.2": {
      "map": {
        "ipaddr.js": "npm:ipaddr.js@1.1.1",
        "forwarded": "npm:forwarded@0.1.0"
      }
    },
    "npm:debug@2.2.0": {
      "map": {
        "ms": "npm:ms@0.7.1"
      }
    },
    "npm:http-errors@1.5.0": {
      "map": {
        "statuses": "npm:statuses@1.3.0",
        "inherits": "npm:inherits@2.0.1",
        "setprototypeof": "npm:setprototypeof@1.0.1"
      }
    },
    "npm:mime-types@2.1.12": {
      "map": {
        "mime-db": "npm:mime-db@1.24.0"
      }
    },
    "npm:jspm-nodelibs-http@0.2.0": {
      "map": {
        "http-browserify": "npm:stream-http@2.5.0"
      }
    },
    "npm:stream-http@2.5.0": {
      "map": {
        "inherits": "npm:inherits@2.0.3",
        "builtin-status-codes": "npm:builtin-status-codes@2.0.0",
        "to-arraybuffer": "npm:to-arraybuffer@1.0.1",
        "xtend": "npm:xtend@4.0.1",
        "readable-stream": "npm:readable-stream@2.1.5"
      }
    },
    "npm:jspm-nodelibs-url@0.2.0": {
      "map": {
        "url-browserify": "npm:url@0.11.0"
      }
    },
    "npm:url@0.11.0": {
      "map": {
        "punycode": "npm:punycode@1.3.2",
        "querystring": "npm:querystring@0.2.0"
      }
    },
    "npm:yargs@6.3.0": {
      "map": {
        "camelcase": "npm:camelcase@3.0.0",
        "set-blocking": "npm:set-blocking@2.0.0",
        "read-pkg-up": "npm:read-pkg-up@1.0.1",
        "os-locale": "npm:os-locale@1.4.0",
        "which-module": "npm:which-module@1.0.0",
        "window-size": "npm:window-size@0.2.0",
        "string-width": "npm:string-width@1.0.2",
        "y18n": "npm:y18n@3.2.1",
        "require-main-filename": "npm:require-main-filename@1.0.1",
        "require-directory": "npm:require-directory@2.1.1",
        "cliui": "npm:cliui@3.2.0",
        "get-caller-file": "npm:get-caller-file@1.0.2",
        "decamelize": "npm:decamelize@1.2.0",
        "yargs-parser": "npm:yargs-parser@4.1.0"
      }
    },
    "npm:cliui@3.2.0": {
      "map": {
        "string-width": "npm:string-width@1.0.2",
        "strip-ansi": "npm:strip-ansi@3.0.1",
        "wrap-ansi": "npm:wrap-ansi@2.0.0"
      }
    },
    "npm:yargs-parser@4.1.0": {
      "map": {
        "camelcase": "npm:camelcase@3.0.0"
      }
    },
    "npm:string-width@1.0.2": {
      "map": {
        "code-point-at": "npm:code-point-at@1.1.0",
        "strip-ansi": "npm:strip-ansi@3.0.1",
        "is-fullwidth-code-point": "npm:is-fullwidth-code-point@1.0.0"
      }
    },
    "npm:os-locale@1.4.0": {
      "map": {
        "lcid": "npm:lcid@1.0.0"
      }
    },
    "npm:read-pkg-up@1.0.1": {
      "map": {
        "read-pkg": "npm:read-pkg@1.1.0",
        "find-up": "npm:find-up@1.1.2"
      }
    },
    "npm:wrap-ansi@2.0.0": {
      "map": {
        "string-width": "npm:string-width@1.0.2"
      }
    },
    "npm:strip-ansi@3.0.1": {
      "map": {
        "ansi-regex": "npm:ansi-regex@2.0.0"
      }
    },
    "npm:read-pkg@1.1.0": {
      "map": {
        "load-json-file": "npm:load-json-file@1.1.0",
        "normalize-package-data": "npm:normalize-package-data@2.3.5",
        "path-type": "npm:path-type@1.1.0"
      }
    },
    "npm:is-fullwidth-code-point@1.0.0": {
      "map": {
        "number-is-nan": "npm:number-is-nan@1.0.1"
      }
    },
    "npm:find-up@1.1.2": {
      "map": {
        "path-exists": "npm:path-exists@2.1.0",
        "pinkie-promise": "npm:pinkie-promise@2.0.1"
      }
    },
    "npm:lcid@1.0.0": {
      "map": {
        "invert-kv": "npm:invert-kv@1.0.0"
      }
    },
    "npm:load-json-file@1.1.0": {
      "map": {
        "pinkie-promise": "npm:pinkie-promise@2.0.1",
        "pify": "npm:pify@2.3.0",
        "graceful-fs": "npm:graceful-fs@4.1.10",
        "strip-bom": "npm:strip-bom@2.0.0",
        "parse-json": "npm:parse-json@2.2.0"
      }
    },
    "npm:path-type@1.1.0": {
      "map": {
        "pinkie-promise": "npm:pinkie-promise@2.0.1",
        "pify": "npm:pify@2.3.0",
        "graceful-fs": "npm:graceful-fs@4.1.10"
      }
    },
    "npm:path-exists@2.1.0": {
      "map": {
        "pinkie-promise": "npm:pinkie-promise@2.0.1"
      }
    },
    "npm:normalize-package-data@2.3.5": {
      "map": {
        "validate-npm-package-license": "npm:validate-npm-package-license@3.0.1",
        "hosted-git-info": "npm:hosted-git-info@2.1.5",
        "is-builtin-module": "npm:is-builtin-module@1.0.0",
        "semver": "npm:semver@5.3.0"
      }
    },
    "npm:pinkie-promise@2.0.1": {
      "map": {
        "pinkie": "npm:pinkie@2.0.4"
      }
    },
    "npm:validate-npm-package-license@3.0.1": {
      "map": {
        "spdx-correct": "npm:spdx-correct@1.0.2",
        "spdx-expression-parse": "npm:spdx-expression-parse@1.0.4"
      }
    },
    "npm:is-builtin-module@1.0.0": {
      "map": {
        "builtin-modules": "npm:builtin-modules@1.1.1"
      }
    },
    "npm:strip-bom@2.0.0": {
      "map": {
        "is-utf8": "npm:is-utf8@0.2.1"
      }
    },
    "npm:parse-json@2.2.0": {
      "map": {
        "error-ex": "npm:error-ex@1.3.0"
      }
    },
    "npm:spdx-correct@1.0.2": {
      "map": {
        "spdx-license-ids": "npm:spdx-license-ids@1.2.2"
      }
    },
    "npm:error-ex@1.3.0": {
      "map": {
        "is-arrayish": "npm:is-arrayish@0.2.1"
      }
    },
    "npm:jspm-nodelibs-domain@0.2.0": {
      "map": {
        "domain-browserify": "npm:domain-browser@1.1.7"
      }
    },
    "npm:source-map-support@0.4.6": {
      "map": {
        "source-map": "npm:source-map@0.5.6"
      }
    },
    "npm:opn@4.0.2": {
      "map": {
        "pinkie-promise": "npm:pinkie-promise@2.0.1",
        "object-assign": "npm:object-assign@4.1.0"
      }
    },
    "npm:compression@1.6.2": {
      "map": {
        "on-headers": "npm:on-headers@1.0.1",
        "vary": "npm:vary@1.1.0",
        "debug": "npm:debug@2.2.0",
        "bytes": "npm:bytes@2.3.0",
        "accepts": "npm:accepts@1.3.3",
        "compressible": "npm:compressible@2.0.9"
      }
    },
    "npm:compressible@2.0.9": {
      "map": {
        "mime-db": "npm:mime-db@1.24.0"
      }
    },
    "npm:jspm-nodelibs-zlib@0.2.0": {
      "map": {
        "zlib-browserify": "npm:browserify-zlib@0.1.4"
      }
    },
    "npm:browserify-zlib@0.1.4": {
      "map": {
        "readable-stream": "npm:readable-stream@2.1.5",
        "pako": "npm:pako@0.2.9"
      }
    },
    "npm:sitemap@1.8.2": {
      "map": {
        "url-join": "npm:url-join@1.1.0",
        "underscore": "npm:underscore@1.8.3"
      }
    }
  }
});
