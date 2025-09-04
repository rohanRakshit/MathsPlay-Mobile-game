#include <jni.h>
#include <string>
#include <vector>
#include <map>
#include <cstdlib>
#include <ctime>
#include <algorithm>

using namespace std;

int mult(string &question){
    int rm1 = rand() % 26;
    int rm2 = rand() % 10;
    question = "Find the product of " + to_string(rm1) + " and " + to_string(rm2) + "\n";
    return rm1 * rm2;
}

int div(string &question){
    int rm1 = rand() % 100;
    int rm2 = rand() % 50 + 1;  // avoid zero
    if(rm1 < rm2){
        swap(rm1, rm2);
    }
    question = "Find the integer quotient of " + to_string(rm1) + " and " + to_string(rm2) + "\n";
    return rm1 / rm2;
}

int root(string &question){
    int rm1 = rand() % 33;
    int q = rm1 * rm1;
    question = "Square root of " + to_string(q) + " is \n";
    return rm1;
}

int basic(char op, string &question){
    int rm1 = rand() % 101;
    int rm2 = rand() % 101;
    if(op == '+'){
        question = "Find the sum of " + to_string(rm1) + " and " + to_string(rm2) + "\n";
        return rm1 + rm2;
    } else {
        if(rm1 < rm2){
            swap(rm1, rm2);
        }
        question = "Find the difference between " + to_string(rm1) + " and " + to_string(rm2) + "\n";
        return rm1 - rm2;
    }
}

map<char,int> choice(int n){
    bool none = (rand() % 4 == 0); // 25% chance for "None of these"
    vector<int> op;
    if(!none){
        op.push_back(n);
    }
    while(op.size() < 4){
        int w = n + rand() % 50 - 25;
        if(find(op.begin(), op.end(), w) == op.end()){
            if(none && w == n) continue;
            op.push_back(w);
        }
    }
    sort(op.begin(), op.end());
    map<char,int> mp;
    for(int i = 0; i < op.size(); i++){
        mp[char(97 + i)] = op[i];
    }
    return mp;
}

string QnA(){
    vector<char> randomOperation = {'+','-','*','/','w','t','r'};
    char op = randomOperation[rand() % randomOperation.size()];
    int rt;
    string question;

    switch(op){
        case '*':
            rt = mult(question);
            break;
        case '/':
            rt = div(question);
            break;
        case 'w':
        {
            int rm1 = rand() % 26;
            rt = rm1 * rm1;
            question = "Find the square of " + to_string(rm1) + "\n";
        }
            break;
        case 't':
        {
            int rm1 = rand() % 16;
            rt = rm1 * rm1 * rm1;
            question = "Find the cube of " + to_string(rm1) + "\n";
        }
            break;
        case 'r':
            rt = root(question);
            break;
        default:
            rt = basic(op, question);
            break;
    }

    map<char,int> options = choice(rt);
    string result = question;
    for(auto &v : options){
        result += "(";
        result += v.first;   // display option letter directly
        result += ") " + to_string(v.second);
        if(v.second == rt) result += "✔";  // <-- mark correct answer
        result += "\t";
    }
    result += "(e) None of these\n";

    return result;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_mathsplay_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {

    srand(time(0));
    string q = QnA();
    return env->NewStringUTF(q.c_str());
}
