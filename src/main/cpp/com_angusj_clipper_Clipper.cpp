
#include "/usr/local/Cellar/openjdk/16.0.1/include/jni.h"

#include <vector>
#include <algorithm>
#include "com_angusj_clipper_Clipper.h"

#include "clipper.h"
#include "clipper_offset.h"
#include "clipper_core.hpp"


JNIEXPORT jobjectArray JNICALL Java_com_angusj_clipper_Clipper_generatePath
        (JNIEnv *env, jobject obj, jobjectArray jPaths, jdouble delta) {

    jdoubleArray jPath = (jdoubleArray)env->GetObjectArrayElement(jPaths, 0);


    jdouble *inputElements = env->GetDoubleArrayElements(jPath, 0);
    int size = env->GetArrayLength(jPath);

    clipperlib::PathD* path = new clipperlib::PathD();
    for(int i = 0 ; i < size - 1; i+=2){
        path->push_back(clipperlib::PointD(inputElements[i],inputElements[i+1]));
    }

    clipperlib::ClipperOffset offset(2.0,0.001);

    offset.AddPath(*path, clipperlib::JoinType::Round, clipperlib::EndType::Polygon);

    clipperlib::PathsD paths = offset.Execute(delta);

    jobjectArray result = env->NewObjectArray(paths.size(), env->GetObjectClass(jPath), 0);

    int i = 0;
    for(auto singlePath : paths.data){
        jdoubleArray resultPath = env->NewDoubleArray(singlePath.size() * 2);
        env->SetObjectArrayElement(result, i, resultPath);

        int j = 0;
        std::vector<double> __c_vec(singlePath.size() * 2);

        for( auto point : singlePath.data ){
            __c_vec[j * 2] = point.x;
            __c_vec[j * 2 + 1] = point.y;
            j++;
        }


        double* __c_ptr = __c_vec.data();
        env->SetDoubleArrayRegion (resultPath, 0, singlePath.size() * 2, reinterpret_cast<jdouble*>(__c_ptr));
        i++;
    }

    return result;
}
