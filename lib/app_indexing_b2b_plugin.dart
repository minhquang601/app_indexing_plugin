import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

const channelName = 'app_indexing_b2b_plugin';

class AppIndexingB2bPlugin {
  static const _channel = MethodChannel(channelName);
  
  Future<void> addProductToAppindexing({
    @required productName,
    @required query,
    @required desc
  }) async {
    var agrs = <String, dynamic>{
      'name': productName ?? '',
      'query' : query ?? '',
      'desc' : desc ?? ''
    };

    return _channel.invokeMethod('updateIndexing', agrs);
  }

}
