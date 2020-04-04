使用 mediaExtractor 分离视频
使用 mediaMuxer 生成视频

参考 https://blog.51cto.com/ticktick/1710743

修改 bufferInfo.flags 值设置 ，没有使用 文章里的 MediaCodec.BUFFER_FLAG_SYNC_FRAME
而是使用的 mediaExtractor.sampleFlags
否则会造成中间有的帧有绿的，拖动视频也会造成卡顿

abc.mp4是手机拍摄的
newabc.mp4是代码处理的
