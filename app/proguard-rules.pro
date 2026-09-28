# Project-specific R8 rules.
# Room, Hilt, Compose and Coil ship their own consumer rules; the app itself
# uses no reflection. Keep line numbers so release crash reports stay readable.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
