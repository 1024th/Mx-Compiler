#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef bool i1;
typedef char i8;
typedef __int32_t i32;

i8* __malloc(i32 __size) { return malloc(__size); }

i8* __str_cat(i8* str1, i8* str2) {
  i8* ret = malloc(strlen(str1) + strlen(str2) + 1);
  strcpy(ret, str1);
  strcat(ret, str2);
  return ret;
}

i1 __str_eq(i8* s1, i8* s2) { return strcmp(s1, s2) == 0; }
i1 __str_ne(i8* s1, i8* s2) { return strcmp(s1, s2) != 0; }
i1 __str_lt(i8* s1, i8* s2) { return strcmp(s1, s2) < 0; }
i1 __str_le(i8* s1, i8* s2) { return strcmp(s1, s2) <= 0; }
i1 __str_gt(i8* s1, i8* s2) { return strcmp(s1, s2) > 0; }
i1 __str_ge(i8* s1, i8* s2) { return strcmp(s1, s2) >= 0; }

i32 __str_length(i8* s) { return strlen(s); }
i8* __str_substring(i8* str, i32 left, i32 right) {
  i32 len = right - left;
  i8* s = malloc(len + 1);
  memcpy(s, str + left, len);
  s[len] = '\0';
  return s;
}
i32 __str_parseInt(i8* s) {
  i32 ret;
  sscanf(s, "%d", &ret);
  return ret;
}
i32 __str_ord(i8* s, int pos) { return s[pos]; }

void print(i8* s) { printf("%s", s); }
void println(i8* s) { printf("%s\n", s); }
void printInt(i32 num) { printf("%d", num); }
void printlnInt(i32 num) { printf("%d\n", num); }

i8* getString() {
  i8* s = malloc(256);
  scanf("%s", s);
  return s;
}
i32 getInt() {
  i32 ret;
  scanf("%d", &ret);
  return ret;
}
i8* toString(i32 num) {
  i8* s = malloc(16);
  sprintf(s, "%d", num);
  return s;
}
