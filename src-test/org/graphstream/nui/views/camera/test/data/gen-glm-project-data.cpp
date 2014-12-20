#include <glm/mat4x4.hpp> // glm::mat4
#include <glm/vec4.hpp>
#include <glm/ext.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/detail/func_geometric.hpp>
#include <GL/gl.h>
#include <iostream>
#include <fstream>
#include <random>
#include <stdexcept>
#include <string>
#include <chrono>

#define PRINTD(d, out) out.write(reinterpret_cast<const char *>(&d), sizeof(double));
#define PRINTA(a, l, out) out.write(reinterpret_cast<const char *>(a), l*sizeof(double));
#define PRINTM(mat, out) out.write(reinterpret_cast<const char *>(glm::value_ptr(mat)), 16*sizeof(double));
#define PRINTV(vec, out) out.write(reinterpret_cast<const char *>(&vec.x), vec.length()*sizeof(double));

//
// Compil with :
// > gcc -lm -lstdc++ -o test-glm -std=gnu++11 $FILE
//

int main(int argc, char** argv) {
	unsigned seed = std::chrono::system_clock::now().time_since_epoch().count();
	std::default_random_engine gen (seed);
	std::uniform_real_distribution<double> d(-64.0, 64.0);
	std::uniform_real_distribution<double> dz(0.01, 64.0);
	std::uniform_real_distribution<double> da(0.1, 2.0);
	std::uniform_real_distribution<double> df(3.14 / 4.0, 3.14);
	std::uniform_int_distribution<int>  dv(100, 4000);
	
	int maxIte = 100;
	
	if (argc < 2) {
		std::cerr << "Usage: " << argv[0] << " file [count]" << std::endl;
		return 1;
	}
	
	std::ofstream fout;
	fout.open (argv[1], std::ios::binary);
	
	if (argc > 2) {
		try {
    		maxIte = std::stoi(std::string(argv[2]));
		}
		catch (const std::invalid_argument& ia) {
			std::cerr << "Invalid argument: " << ia.what() << '\n';
			return 1;
		}
	}
		
	
	for (int ite = 0; ite < maxIte; ite++) {
		glm::dvec3 eye(
			d(gen), d(gen), d(gen));
		glm::dvec3 cen(
			d(gen), d(gen), d(gen));
		glm::dvec3 upv(
			d(gen), d(gen), d(gen));
		
		upv = glm::normalize(upv);
		
		glm::dmat4 view = glm::lookAt(eye, cen, upv);
		
		double fovy   = df(gen);
		double aspect = da(gen);
		double zNear  = dz(gen);
		double zFar   = zNear + dz(gen);
		
		glm::dmat4 proj = glm::perspective(fovy, aspect, zNear, zFar);
		
		glm::dvec4 viewport(
			static_cast<double>(dv(gen)),
			static_cast<double>(dv(gen)),
			static_cast<double>(dv(gen)),
			static_cast<double>(dv(gen)));
		
		glm::dvec3 point (d(gen), d(gen), d(gen));
		glm::dvec3 projected = glm::project(point, view, proj, viewport);
		
		PRINTV(point,     fout)
		PRINTM(view,      fout)
		PRINTM(proj,      fout)
		PRINTV(viewport,  fout)
		PRINTV(projected, fout)
	}
	
	fout.close();
	
	return 0;
}

